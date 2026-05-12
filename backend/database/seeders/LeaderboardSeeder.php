<?php

namespace Database\Seeders;

use App\Models\Exercise;
use App\Models\Submission;
use App\Models\SubmissionResult;
use App\Models\User;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;

class LeaderboardSeeder extends Seeder
{
    public function run(): void
    {
        $exercises = Exercise::where('is_published', true)->get();

        if ($exercises->isEmpty()) return;

        $students = [
            ['name' => 'Raúl Rivera',     'email' => 'raul@demo.com',    'solved' => 12],
            ['name' => 'María González',  'email' => 'maria@demo.com',   'solved' => 10],
            ['name' => 'Carlos Pérez',    'email' => 'carlos@demo.com',  'solved' => 8],
            ['name' => 'Ana Martínez',    'email' => 'ana@demo.com',     'solved' => 7],
            ['name' => 'Lucía Fernández', 'email' => 'lucia@demo.com',   'solved' => 6],
            ['name' => 'Javier López',    'email' => 'javier@demo.com',  'solved' => 5],
            ['name' => 'Sofía Ruiz',      'email' => 'sofia@demo.com',   'solved' => 4],
            ['name' => 'Diego Sánchez',   'email' => 'diego@demo.com',   'solved' => 3],
            ['name' => 'Paula Torres',    'email' => 'paula@demo.com',   'solved' => 2],
            ['name' => 'Marcos Díaz',     'email' => 'marcos@demo.com',  'solved' => 1],
        ];

        $languages = ['javascript', 'python', 'java'];

        foreach ($students as $data) {
            $user = User::firstOrCreate(
                ['email' => $data['email']],
                [
                    'name'     => $data['name'],
                    'password' => Hash::make('password'),
                    'role'     => 'student',
                ]
            );

            $toSolve = $exercises->shuffle()->take($data['solved']);

            foreach ($toSolve as $exercise) {
                // Evitar duplicados si se re-ejecuta el seeder
                $exists = Submission::where('user_id', $user->id)
                    ->where('exercise_id', $exercise->id)
                    ->where('status', 'accepted')
                    ->exists();

                if ($exists) continue;

                $testCases  = $exercise->testCases()->orderBy('id')->get();
                $execTime   = round(mt_rand(80, 300) / 1000, 3);
                $memory     = mt_rand(7500, 14000);

                $submission = Submission::create([
                    'user_id'            => $user->id,
                    'exercise_id'        => $exercise->id,
                    'code'               => '// solución demo',
                    'language'           => $languages[array_rand($languages)],
                    'status'             => 'accepted',
                    'max_execution_time' => $execTime,
                    'max_memory'         => $memory,
                ]);

                foreach ($testCases as $tc) {
                    SubmissionResult::create([
                        'submission_id'  => $submission->id,
                        'test_case_id'   => $tc->id,
                        'passed'         => true,
                        'output'         => $tc->expected_output,
                        'error'          => null,
                        'status'         => 'accepted',
                        'execution_time' => round(mt_rand(50, 200) / 1000, 3),
                        'memory'         => mt_rand(7000, 13000),
                    ]);
                }
            }
        }
    }
}
