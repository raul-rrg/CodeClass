<?php

namespace Database\Seeders;

use App\Models\Exercise;
use App\Models\User;
use Illuminate\Database\Seeder;

class ExerciseSeeder extends Seeder
{
    public function run(): void
    {
        $teacher = User::where('role', 'teacher')->first()
            ?? User::first();

        $exercises = [

            [
                'title'         => 'Sum Two Numbers',
                'description'   => "Dados dos enteros `a` y `b`, devuelve su suma.\n\n**Ejemplo:**\n```\nEntrada:  a = 3, b = 5\nSalida:   8\n```",
                'difficulty'    => 'easy',
                'category'      => 'Matemáticas',
                'function_name' => 'sum',
                'solution_code'     => "function sum(a, b) {\n  return a + b;\n}",
                'solution_language' => 'javascript',
                'is_verified'   => true,
                'is_published'  => true,
                'test_cases'    => [
                    ['input' => [3, 5],    'expected_output' => '8',   'is_hidden' => false],
                    ['input' => [10, 20],  'expected_output' => '30',  'is_hidden' => false],
                    ['input' => [-1, 1],   'expected_output' => '0',   'is_hidden' => true],
                    ['input' => [0, 1],    'expected_output' => '1',   'is_hidden' => true],
                ],
            ],

            [
                'title'         => 'Fibonacci',
                'description'   => "Dado un entero `n`, devuelve el n-ésimo número de Fibonacci.\n\nLa secuencia empieza: 1, 1, 2, 3, 5, 8, 13, ...\n\n**Ejemplo:**\n```\nEntrada:  n = 6\nSalida:   8\n```",
                'difficulty'    => 'easy',
                'category'      => 'Matemáticas',
                'function_name' => 'fibonacci',
                'solution_code'     => "function fibonacci(n) {\n  if (n <= 1) return 1;\n  let a = 1, b = 1;\n  for (let i = 2; i < n; i++) {\n    [a, b] = [b, a + b];\n  }\n  return b;\n}",
                'solution_language' => 'javascript',
                'is_verified'   => true,
                'is_published'  => true,
                'test_cases'    => [
                    ['input' => [1],  'expected_output' => '1',  'is_hidden' => false],
                    ['input' => [2],  'expected_output' => '1',  'is_hidden' => false],
                    ['input' => [6],  'expected_output' => '8',  'is_hidden' => false],
                    ['input' => [10], 'expected_output' => '55', 'is_hidden' => true],
                ],
            ],

        ];

        foreach ($exercises as $data) {
            $testCases = $data['test_cases'];
            unset($data['test_cases']);

            $exercise = Exercise::create(array_merge($data, ['user_id' => $teacher->id]));

            foreach ($testCases as $tc) {
                $exercise->testCases()->create($tc);
            }
        }
    }
}
