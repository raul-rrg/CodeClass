<?php

namespace App\Jobs;

use App\Models\Submission;
use App\Models\SubmissionResult;
use App\Services\JudgeService;
use Illuminate\Contracts\Queue\ShouldQueue;
use Illuminate\Foundation\Queue\Queueable;

class EvaluateSubmission implements ShouldQueue
{
    use Queueable;

    // Reintentar hasta 3 veces si falla (ej: Judge0 no disponible)
    public int $tries   = 3;
    public int $timeout = 60;

    public function __construct(public Submission $submission) {}

    public function handle(JudgeService $judge): void
    {
        // Limpiamos resultados previos por si el job se reintenta
        $this->submission->submissionResults()->delete();

        $exercise  = $this->submission->exercise;
        $testCases = $exercise->testCases;

        $allPassed     = true;
        $compileOutput = '';

        foreach ($testCases as $case) {
            $result = $judge->runTestCase(
                $this->submission->language->value,
                $this->submission->code,
                $exercise->function_name,
                $exercise->parameters,
                $exercise->return_type,
                $case
            );

            SubmissionResult::create([
                'submission_id'  => $this->submission->id,
                'test_case_id'   => $case->id,
                'passed'         => $result['passed'],
                'output'         => $result['output'],
                'error'          => $result['error'],
                'status'         => $result['status'],
                'execution_time' => $result['time'],
                'memory'         => $result['memory'],
            ]);

            if (!$result['passed']) $allPassed = false;
            $compileOutput .= $result['compile_output'];
        }

        $results = $this->submission->submissionResults();

        $this->submission->update([
            'status'             => $allPassed ? 'accepted' : 'rejected',
            'compile_output'     => $compileOutput,
            'max_execution_time' => $results->max('execution_time'),
            'max_memory'         => $results->max('memory'),
        ]);
    }

    // Si el Job falla después de los reintentos, marcamos la submission como error
    public function failed(\Throwable $e): void
    {
        $this->submission->update(['status' => 'error']);
    }
}
