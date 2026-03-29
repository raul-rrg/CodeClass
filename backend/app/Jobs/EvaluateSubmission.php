<?php

namespace App\Jobs;

use App\Models\Submission;
use App\Models\SubmissionResult;
use Illuminate\Contracts\Queue\ShouldQueue;
use Illuminate\Foundation\Queue\Queueable;
use Illuminate\Support\Facades\Http;

class EvaluateSubmission implements ShouldQueue
{
    use Queueable;

    // Reintentar hasta 3 veces si falla
    public int $tries = 3;

    // Tiempo máximo de ejecución del Job (en segundos)
    public int $timeout = 60;



    public Submission $submission;


    public function __construct(Submission $submission)
    {
        $this->submission = $submission;
    }

    public function handle(): void
    {
        // Obtenemos los casos de prueba del ejercicio asociado a la submission
        $testCases = $this->submission->exercise->testCases;

        $allPassed = true;
        $compileOutput = '';

        // Iteramos sobre cada caso de prueba para evaluarlo
        foreach ($testCases as $case) {

            // Enviamos el código de la submission a Judge0 para que lo ejecute y compare el output con el esperado en el caso de prueba actual
            $response = Http::post(config('services.judge0.url') . '/submissions?wait=true', [
                'source_code'     => $this->submission->code,
                'language_id'     => $this->submission->language->judge0Id(),
                'stdin'           => $case->input,
                'expected_output' => $case->expected_output,
            ]);

            // Si la respuesta de Judge0 no es exitosa o no contiene el campo 'status', lanzamos una excepción para que el Job se reintente
            if ($response->failed() || !isset($response['status'])) {
                throw new \RuntimeException('Judge0 error: ' . $response->body());
            }

            // Guardamos el resultado en SubmissionResult
            SubmissionResult::create([
                'submission_id' => $this->submission->id,
                'test_case_id'  => $case->id,
                'passed'        => $response['status']['id'] === 3, // Judge0 devuelve id 3 cuando el código es correcto (Accepted)
                'output'        => $response['stdout'],
                'error'         => $response['stderr'],
            ]);

            // Si alguno de los casos de prueba no pasa, marcamos como no exitosa
            if ($response['status']['id'] !== 3) {
                $allPassed = false;
            }

            // Concatenamos el output de compilación (si lo hay)
            $compileOutput .= $response['compile_output'] ?? '';
        }

        // Actualizamos el status final de la submission
        $this->submission->update([
            'status'         => $allPassed ? 'accepted' : 'rejected',
            'compile_output' => $compileOutput,
        ]);
    }

    // Si el Job falla después de los reintentos, actualizamos el status de la submission a 'error'
    public function failed(\Throwable $e): void
    {
        $this->submission->update(['status' => 'error']);
    }
}
