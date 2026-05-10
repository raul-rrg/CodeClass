<?php

namespace Database\Seeders;

use App\Models\Exercise;
use App\Models\Submission;
use App\Models\SubmissionResult;
use App\Models\User;
use Carbon\Carbon;
use Illuminate\Database\Seeder;

class SubmissionSeeder extends Seeder
{
    public function run(): void
    {
        $exercise = Exercise::where('function_name', 'factorial')->first();
        if (!$exercise) return;

        $testCases = $exercise->testCases()->orderBy('id')->get();
        if ($testCases->isEmpty()) return;

        // email => [ [status, passCount, minutesAgo, language], ... ]
        $scenarios = [
            'student@example.com' => [
                ['rejected', 2, 180, 'python'],
                ['rejected', 4, 90,  'python'],
                ['accepted', 5, 20,  'python'],
            ],
            'raul@demo.com' => [
                ['rejected', 3, 260, 'javascript'],
                ['accepted', 5, 200, 'javascript'],
            ],
            'maria@demo.com' => [
                ['rejected', 1, 320, 'python'],
                ['rejected', 3, 240, 'python'],
            ],
            'carlos@demo.com' => [
                ['accepted', 5, 110, 'java'],
            ],
            'ana@demo.com' => [
                ['rejected', 0, 500, 'python'],
                ['rejected', 2, 420, 'python'],
                ['rejected', 4, 340, 'python'],
            ],
            'lucia@demo.com' => [
                ['rejected', 3, 600, 'javascript'],
                ['accepted', 5, 540, 'javascript'],
            ],
            'javier@demo.com' => [
                ['rejected', 2, 700, 'java'],
            ],
            'sofia@demo.com' => [
                ['accepted', 5, 80, 'python'],
            ],
        ];

        foreach ($scenarios as $email => $attempts) {
            $user = User::where('email', $email)->first();
            if (!$user) continue;

            // Skip si ya tiene submissions con resultados (seeder ya ejecutado)
            $hasDetailed = Submission::where('user_id', $user->id)
                ->where('exercise_id', $exercise->id)
                ->whereHas('submissionResults')
                ->exists();
            if ($hasDetailed) continue;

            foreach ($attempts as $attempt) {
                [$status, $passCount, $minutesAgo, $language] = $attempt;

                $accepted = $status === 'accepted';
                $time     = Carbon::now()->subMinutes($minutesAgo);

                $submission             = new Submission();
                $submission->user_id    = $user->id;
                $submission->exercise_id = $exercise->id;
                $submission->code       = $accepted ? $this->correctCode($language) : $this->wrongCode($language);
                $submission->language   = $language;
                $submission->status     = $status;
                $submission->max_execution_time = round(mt_rand(80, 350) / 1000, 3);
                $submission->max_memory         = mt_rand(7500, 14000);
                $submission->created_at = $time;
                $submission->updated_at = $time;
                $submission->save();

                foreach ($testCases as $i => $tc) {
                    $passed = $i < $passCount;
                    SubmissionResult::create([
                        'submission_id'  => $submission->id,
                        'test_case_id'   => $tc->id,
                        'passed'         => $passed,
                        'output'         => $passed ? $tc->expected_output : $this->wrongOutput($tc->expected_output),
                        'error'          => null,
                        'status'         => $passed ? 'accepted' : 'wrong_answer',
                        'execution_time' => round(mt_rand(50, 180) / 1000, 3),
                        'memory'         => mt_rand(7500, 13000),
                    ]);
                }
            }
        }
    }

    private function correctCode(string $lang): string
    {
        return match ($lang) {
            'python'     => "def factorial(n):\n    if n == 0:\n        return 1\n    return n * factorial(n - 1)",
            'javascript' => "function factorial(n) {\n    if (n === 0) return 1;\n    return n * factorial(n - 1);\n}",
            default      => "public static int factorial(int n) {\n    if (n == 0) return 1;\n    return n * factorial(n - 1);\n}",
        };
    }

    private function wrongCode(string $lang): string
    {
        // Bug clásico: range(1, n) en vez de range(1, n+1) → da (n-1)!
        return match ($lang) {
            'python'     => "def factorial(n):\n    result = 1\n    for i in range(1, n):\n        result *= i\n    return result",
            'javascript' => "function factorial(n) {\n    let result = 1;\n    for (let i = 1; i < n; i++) result *= i;\n    return result;\n}",
            default      => "public static int factorial(int n) {\n    int result = 1;\n    for (int i = 1; i < n; i++) result *= i;\n    return result;\n}",
        };
    }

    private function wrongOutput(string $expected): string
    {
        // Simula el bug range(1,n): devuelve (n-1)! para n>=2, y 1 para n<2
        $wrongMap = ['120' => '24', '5040' => '720', '3628800' => '362880', '1' => '0'];
        return $wrongMap[$expected] ?? (string) max(0, (int) $expected - 1);
    }
}
