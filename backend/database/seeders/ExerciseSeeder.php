<?php

namespace Database\Seeders;

use App\Models\Exercise;
use App\Models\User;
use App\Services\TemplateGenerator;
use Illuminate\Database\Seeder;

class ExerciseSeeder extends Seeder
{
    public function run(): void
    {
        $teacher   = User::where('role', 'teacher')->first() ?? User::first();
        $generator = new TemplateGenerator();

        $exercises = [

            [
                'title'         => 'Sum Two Numbers',
                'description'   => "Dados dos enteros `a` y `b`, devuelve su suma.\n\n**Ejemplo:**\n```\nEntrada:  a = 3, b = 5\nSalida:   8\n```",
                'difficulty'    => 'easy',
                'category'      => 'Matemáticas',
                'function_name' => 'sum',
                'parameters'    => [['name' => 'a', 'type' => 'int'], ['name' => 'b', 'type' => 'int']],
                'return_type'   => 'int',
                'solution_code'     => "function sum(a, b) {\n  return a + b;\n}",
                'solution_language' => 'javascript',
                'is_verified'   => true,
                'is_published'  => true,
                'test_cases'    => [
                    ['input' => [3, 5],   'expected_output' => '8',  'is_hidden' => false],
                    ['input' => [10, 20], 'expected_output' => '30', 'is_hidden' => false],
                    ['input' => [-1, 1],  'expected_output' => '0',  'is_hidden' => true],
                    ['input' => [0, 1],   'expected_output' => '1',  'is_hidden' => true],
                ],
            ],

            [
                'title'         => 'Reverse String',
                'description'   => "Dada una cadena de texto `s`, devuelve la cadena invertida.\n\n**Ejemplo:**\n```\nEntrada:  s = \"hello\"\nSalida:   \"olleh\"\n```",
                'difficulty'    => 'easy',
                'category'      => 'Cadenas',
                'function_name' => 'reverseString',
                'parameters'    => [['name' => 's', 'type' => 'string']],
                'return_type'   => 'string',
                'solution_code'     => "function reverseString(s) {\n  return s.split('').reverse().join('');\n}",
                'solution_language' => 'javascript',
                'is_verified'   => true,
                'is_published'  => true,
                'test_cases'    => [
                    ['input' => ['hello'],   'expected_output' => 'olleh',  'is_hidden' => false],
                    ['input' => ['world'],   'expected_output' => 'dlrow',  'is_hidden' => false],
                    ['input' => ['abcde'],   'expected_output' => 'edcba',  'is_hidden' => false],
                    ['input' => ['racecar'], 'expected_output' => 'racecar','is_hidden' => true],
                    ['input' => [''],        'expected_output' => '',       'is_hidden' => true],
                ],
            ],

            [
                'title'         => 'Triangle of Stars',
                'description'   => "Dado un entero `n`, devuelve un triángulo de asteriscos de `n` filas separadas por saltos de línea.\n\n**Ejemplo:**\n```\nEntrada: n = 3\nSalida:\n*\n**\n***\n```\n\nCada fila `i` contiene exactamente `i` asteriscos (empezando en 1).",
                'difficulty'    => 'easy',
                'category'      => 'Cadenas',
                'function_name' => 'triangle',
                'parameters'    => [['name' => 'n', 'type' => 'int']],
                'return_type'   => 'string',
                'solution_code'     => "function triangle(n) {\n  return Array.from({ length: n }, (_, i) => '*'.repeat(i + 1)).join('\\n');\n}",
                'solution_language' => 'javascript',
                'is_verified'   => true,
                'is_published'  => true,
                'test_cases'    => [
                    ['input' => [1], 'expected_output' => '*',                 'is_hidden' => false],
                    ['input' => [3], 'expected_output' => "*\n**\n***",        'is_hidden' => false],
                    ['input' => [5], 'expected_output' => "*\n**\n***\n****\n*****", 'is_hidden' => false],
                    ['input' => [2], 'expected_output' => "*\n**",             'is_hidden' => true],
                    ['input' => [4], 'expected_output' => "*\n**\n***\n****",  'is_hidden' => true],
                ],
            ],

            [
                'title'         => 'FizzBuzz',
                'description'   => "Dado un entero `n`, devuelve:\n- `\"FizzBuzz\"` si es divisible por 3 y por 5\n- `\"Fizz\"` si solo es divisible por 3\n- `\"Buzz\"` si solo es divisible por 5\n- El número como string en cualquier otro caso\n\n**Ejemplo:**\n```\nEntrada: n = 15\nSalida:  \"FizzBuzz\"\n\nEntrada: n = 7\nSalida:  \"7\"\n```",
                'difficulty'    => 'easy',
                'category'      => 'Lógica',
                'function_name' => 'fizzBuzz',
                'parameters'    => [['name' => 'n', 'type' => 'int']],
                'return_type'   => 'string',
                'solution_code'     => "function fizzBuzz(n) {\n  if (n % 15 === 0) return 'FizzBuzz';\n  if (n % 3 === 0)  return 'Fizz';\n  if (n % 5 === 0)  return 'Buzz';\n  return String(n);\n}",
                'solution_language' => 'javascript',
                'is_verified'   => true,
                'is_published'  => true,
                'test_cases'    => [
                    ['input' => [1],  'expected_output' => '1',        'is_hidden' => false],
                    ['input' => [3],  'expected_output' => 'Fizz',     'is_hidden' => false],
                    ['input' => [5],  'expected_output' => 'Buzz',     'is_hidden' => false],
                    ['input' => [15], 'expected_output' => 'FizzBuzz', 'is_hidden' => false],
                    ['input' => [7],  'expected_output' => '7',        'is_hidden' => true],
                    ['input' => [30], 'expected_output' => 'FizzBuzz', 'is_hidden' => true],
                ],
            ],

            [
                'title'         => 'Fibonacci',
                'description'   => "Dado un entero `n`, devuelve el n-ésimo número de Fibonacci.\n\nLa secuencia empieza: 1, 1, 2, 3, 5, 8, 13, ...\n\n**Ejemplo:**\n```\nEntrada:  n = 6\nSalida:   8\n```",
                'difficulty'    => 'easy',
                'category'      => 'Matemáticas',
                'function_name' => 'fibonacci',
                'parameters'    => [['name' => 'n', 'type' => 'int']],
                'return_type'   => 'int',
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

            // Generamos el template por lenguaje a partir de la firma del ejercicio
            $data['templates'] = $generator->generate(
                $data['function_name'],
                $data['parameters'],
                $data['return_type']
            );

            $exercise = Exercise::create(array_merge($data, ['user_id' => $teacher->id]));

            foreach ($testCases as $tc) {
                $exercise->testCases()->create($tc);
            }
        }
    }
}
