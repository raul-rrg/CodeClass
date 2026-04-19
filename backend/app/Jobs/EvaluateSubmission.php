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

    // Límites fijos de ejecución — el profesor no los controla
    const TIME_LIMIT   = 5;   // segundos
    const MEMORY_LIMIT = 256; // MB

    public Submission $submission;


    public function __construct(Submission $submission)
    {
        $this->submission = $submission;
    }

    public function handle(): void
    {
        // Limpiamos resultados previos por si el job se reintenta
        $this->submission->submissionResults()->delete();

        // Obtenemos los casos de prueba del ejercicio asociado a la submission
        $exercise  = $this->submission->exercise;
        $testCases = $exercise->testCases;

        $allPassed     = true;
        $compileOutput = '';

        // Iteramos sobre cada caso de prueba para evaluarlo
        foreach ($testCases as $case) {

            // Concatenamos el wrapper al código del alumno para que Judge0 pueda llamar a la función
            $code = $this->submission->code . "\n" . $this->buildWrapper($this->submission->language->value, $exercise->function_name);

            // Enviamos el código de la submission a Judge0 para que lo ejecute y compare el output con el esperado en el caso de prueba actual
            // base64_encoded=true para evitar errores con caracteres especiales en el output
            $response = Http::post(config('services.judge0.url') . '/submissions?wait=true&base64_encoded=true', [
                'source_code'     => base64_encode($code),
                'language_id'     => $this->submission->language->judge0Id(),
                'stdin'           => base64_encode(is_array($case->input) ? json_encode($case->input) : $case->input),
                'expected_output' => base64_encode($case->expected_output),
                'cpu_time_limit'  => self::TIME_LIMIT,
                'memory_limit'    => self::MEMORY_LIMIT * 1024, // Judge0 espera KB
            ]);

            // Si la respuesta de Judge0 no es exitosa o no contiene el campo 'status', lanzamos una excepción para que el Job se reintente
            if ($response->failed() || !isset($response['status'])) {
                throw new \RuntimeException('Judge0 error: ' . $response->body());
            }

            $statusId = $response['status']['id'];
            // Judge0 devuelve stdout/stderr en base64 — decodificamos antes de usar
            $stdout   = isset($response['stdout'])   ? base64_decode($response['stdout'])   : '';
            $stderr   = isset($response['stderr'])   ? base64_decode($response['stderr'])   : '';
            $compile  = isset($response['compile_output']) ? base64_decode($response['compile_output']) : '';

            // Presentation Error: status 4 (Wrong Answer) pero el output coincide tras eliminar espacios
            if ($statusId === 4 && trim($stdout) === trim($case->expected_output)) {
                $statusId = 'presentation_error';
                $passed   = false;
            } else {
                $passed = $statusId === 3;
            }

            $status = match (true) {
                $statusId === 3                    => 'accepted',
                $statusId === 'presentation_error' => 'presentation_error',
                $statusId === 7                    => 'memory_limit_exceeded',
                $statusId === 14                   => 'output_too_long',
                default                            => 'wrong_answer',
            };

            // Guardamos el resultado en SubmissionResult
            SubmissionResult::create([
                'submission_id' => $this->submission->id,
                'test_case_id'  => $case->id,
                'passed'        => $passed,
                'output'        => $stdout,
                'error'         => $stderr,
                'status'        => $status,
            ]);

            // Si alguno de los casos de prueba no pasa, marcamos como no exitosa
            if (!$passed) {
                $allPassed = false;
            }

            // Concatenamos el output de compilación (si lo hay)
            $compileOutput .= $compile;
        }

        // Actualizamos el status final de la submission
        $this->submission->update([
            'status'         => $allPassed ? 'accepted' : 'rejected',
            'compile_output' => $compileOutput,
        ]);
    }

    // Genera el wrapper para el lenguaje indicado.
    // El wrapper es codigo que se concatena al codigo del alumno antes de enviarlo a Judge0.
    // Su funcion es llamar a la funcion del alumno con los argumentos del test case
    // e imprimir el resultado por stdout para que Judge0 lo pueda comparar con el expected_output.
    private function buildWrapper(string $language, string $functionName): string
    {
        // Segun el lenguaje del alumno devuelve un template de wrapper distinto.
        // $functionName se interpolara con el nombre real de la funcion del ejercicio,
        // por ejemplo "reverseString" o "fibonacci".
        return match ($language) {

            // Wrapper para JavaScript:
            // 1. Lee el input del test case desde stdin (Judge0 lo inyecta ahi)
            // 2. Lo parsea de JSON string a array de JS: ["hello"] → ["hello"]
            // 3. Desempaqueta el array como argumentos separados con ... y llama a la funcion
            // 4. Serializa el resultado a JSON y lo imprime por stdout para que Judge0 lo lea
            'javascript' => <<<JS
            const __args = JSON.parse(require('fs').readFileSync('/dev/stdin', 'utf8'));
            console.log(JSON.stringify({$functionName}(...__args)));
            JS,

            // Wrapper para Python:
            // 1. Lee el input del test case desde stdin y lo parsea directamente a lista de Python
            // 2. Desempaqueta la lista como argumentos separados con * y llama a la funcion
            // 3. Serializa el resultado a JSON y lo imprime por stdout para que Judge0 lo lea
            'python' => <<<PY
            import json, sys
            __args = json.load(sys.stdin)
            print(json.dumps({$functionName}(*__args)))
            PY,

            // Si el lenguaje no esta soportado devuelve string vacio
            default => '',
        };
    }


    // Si el Job falla después de los reintentos, actualizamos el status de la submission a 'error'
    public function failed(\Throwable $e): void
    {
        $this->submission->update(['status' => 'error']);
    }
}
