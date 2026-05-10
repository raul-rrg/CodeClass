<?php

namespace Database\Seeders;

use App\Jobs\TranslateExerciseJob;
use App\Models\Exercise;
use App\Models\User;
use App\Services\TemplateGenerator;
use Illuminate\Database\Seeder;

class ExerciseSeeder extends Seeder
{
    public function run(): void
    {
        app()->setLocale('es');

        $teacher   = User::where('role', 'teacher')->first() ?? User::first();
        $generator = new TemplateGenerator();

        $exercises = [

            // ── FÁCIL: 3 ejercicios para torneo (2 privados) ─────────────────

            [
                'title'             => 'Suma de Dos Números',
                'short_description' => 'Devuelve la suma de dos enteros.',
                'description'       => "Dados dos enteros `a` y `b`, devuelve su suma.\n\n**Ejemplo:**\n```\nEntrada:  a = 3, b = 5\nSalida:   8\n```",
                'difficulty'        => 'easy',
                'category'          => 'math',
                'function_name'     => 'sumar',
                'parameters'        => [['name' => 'a', 'type' => 'int'], ['name' => 'b', 'type' => 'int']],
                'return_type'       => 'int',
                'is_verified'       => true,
                'is_published'      => true,
                'test_cases'        => [
                    ['input' => [1, 2],     'expected_output' => '3',   'is_hidden' => false],
                    ['input' => [5, 10],    'expected_output' => '15',  'is_hidden' => false],
                    ['input' => [-3, 3],    'expected_output' => '0',   'is_hidden' => false],
                    ['input' => [0, 0],     'expected_output' => '0',   'is_hidden' => true],
                    ['input' => [100, 200], 'expected_output' => '300', 'is_hidden' => true],
                ],
            ],

            [
                'title'             => '¿Es Par o Impar?',
                'short_description' => 'Indica si un número entero es par.',
                'description'       => "Dado un entero `n`, devuelve `true` si es par o `false` si es impar.\n\n**Ejemplo:**\n```\nEntrada:  n = 4\nSalida:   true\n\nEntrada:  n = 7\nSalida:   false\n```",
                'difficulty'        => 'easy',
                'category'          => 'math',
                'function_name'     => 'esPar',
                'parameters'        => [['name' => 'n', 'type' => 'int']],
                'return_type'       => 'bool',
                'is_verified'       => true,
                'is_published'      => false,  // privado — solo visible en torneo
                'test_cases'        => [
                    ['input' => [2],  'expected_output' => 'true',  'is_hidden' => false],
                    ['input' => [7],  'expected_output' => 'false', 'is_hidden' => false],
                    ['input' => [0],  'expected_output' => 'true',  'is_hidden' => true],
                    ['input' => [-4], 'expected_output' => 'true',  'is_hidden' => true],
                    ['input' => [13], 'expected_output' => 'false', 'is_hidden' => true],
                ],
            ],

            [
                'title'             => 'Invertir una Cadena',
                'short_description' => 'Devuelve la cadena escrita al revés.',
                'description'       => "Dada una cadena de texto `s`, devuelve la cadena invertida.\n\n**Ejemplo:**\n```\nEntrada:  s = \"hola\"\nSalida:   \"aloh\"\n```",
                'difficulty'        => 'easy',
                'category'          => 'strings',
                'function_name'     => 'invertir',
                'parameters'        => [['name' => 's', 'type' => 'string']],
                'return_type'       => 'string',
                'is_verified'       => true,
                'is_published'      => false,  // privado — solo visible en torneo
                'test_cases'        => [
                    ['input' => ['hola'],    'expected_output' => 'aloh',    'is_hidden' => false],
                    ['input' => ['mundo'],   'expected_output' => 'odnum',   'is_hidden' => false],
                    ['input' => ['abcde'],   'expected_output' => 'edcba',   'is_hidden' => false],
                    ['input' => ['a'],       'expected_output' => 'a',       'is_hidden' => true],
                    ['input' => ['racecar'], 'expected_output' => 'racecar', 'is_hidden' => true],
                ],
            ],

            // ── MATEMÁTICAS ───────────────────────────────────────────────────

            [
                'title'             => 'Factorial de un Número',
                'short_description' => 'Calcula el factorial de n (n!).',
                'description'       => "Dado un entero no negativo `n`, devuelve su factorial.\n\nEl factorial se define como:\n- `0! = 1`\n- `n! = n × (n−1) × ... × 1`\n\n**Ejemplo:**\n```\nEntrada:  n = 5\nSalida:   120\n```",
                'difficulty'        => 'easy',
                'category'          => 'math',
                'function_name'     => 'factorial',
                'parameters'        => [['name' => 'n', 'type' => 'int']],
                'return_type'       => 'int',
                'is_verified'       => true,
                'is_published'      => true,
                'test_cases'        => [
                    ['input' => [0],  'expected_output' => '1',       'is_hidden' => false],
                    ['input' => [1],  'expected_output' => '1',       'is_hidden' => false],
                    ['input' => [5],  'expected_output' => '120',     'is_hidden' => false],
                    ['input' => [7],  'expected_output' => '5040',    'is_hidden' => true],
                    ['input' => [10], 'expected_output' => '3628800', 'is_hidden' => true],
                ],
            ],

            [
                'title'             => 'Número de Fibonacci',
                'short_description' => 'Devuelve el n-ésimo número de la secuencia de Fibonacci.',
                'description'       => "Dado un entero positivo `n`, devuelve el n-ésimo número de la secuencia de Fibonacci.\n\nLa secuencia empieza en: `1, 1, 2, 3, 5, 8, 13, 21, ...`\n\n**Ejemplo:**\n```\nEntrada:  n = 6\nSalida:   8\n```",
                'difficulty'        => 'easy',
                'category'          => 'math',
                'function_name'     => 'fibonacci',
                'parameters'        => [['name' => 'n', 'type' => 'int']],
                'return_type'       => 'int',
                'is_verified'       => true,
                'is_published'      => true,
                'test_cases'        => [
                    ['input' => [1],  'expected_output' => '1',   'is_hidden' => false],
                    ['input' => [2],  'expected_output' => '1',   'is_hidden' => false],
                    ['input' => [6],  'expected_output' => '8',   'is_hidden' => false],
                    ['input' => [10], 'expected_output' => '55',  'is_hidden' => true],
                    ['input' => [12], 'expected_output' => '144', 'is_hidden' => true],
                ],
            ],

            [
                'title'             => '¿Es Número Primo?',
                'short_description' => 'Determina si un número entero es primo.',
                'description'       => "Dado un entero `n`, devuelve `true` si es primo o `false` en caso contrario.\n\nUn número es primo si es mayor que 1 y solo divisible por 1 y por sí mismo.\n\n**Ejemplo:**\n```\nEntrada:  n = 7\nSalida:   true\n\nEntrada:  n = 4\nSalida:   false\n```",
                'difficulty'        => 'medium',
                'category'          => 'math',
                'function_name'     => 'esPrimo',
                'parameters'        => [['name' => 'n', 'type' => 'int']],
                'return_type'       => 'bool',
                'is_verified'       => true,
                'is_published'      => true,
                'test_cases'        => [
                    ['input' => [2],   'expected_output' => 'true',  'is_hidden' => false],
                    ['input' => [7],   'expected_output' => 'true',  'is_hidden' => false],
                    ['input' => [4],   'expected_output' => 'false', 'is_hidden' => false],
                    ['input' => [1],   'expected_output' => 'false', 'is_hidden' => true],
                    ['input' => [13],  'expected_output' => 'true',  'is_hidden' => true],
                    ['input' => [100], 'expected_output' => 'false', 'is_hidden' => true],
                ],
            ],

            [
                'title'             => 'Máximo Común Divisor',
                'short_description' => 'Calcula el MCD de dos enteros positivos.',
                'description'       => "Dados dos enteros positivos `a` y `b`, devuelve su máximo común divisor (MCD).\n\nPuedes usar el algoritmo de Euclides:\n- Si `b == 0`, el MCD es `a`\n- Si no, el MCD es `mcd(b, a % b)`\n\n**Ejemplo:**\n```\nEntrada:  a = 12, b = 8\nSalida:   4\n```",
                'difficulty'        => 'medium',
                'category'          => 'math',
                'function_name'     => 'mcd',
                'parameters'        => [['name' => 'a', 'type' => 'int'], ['name' => 'b', 'type' => 'int']],
                'return_type'       => 'int',
                'is_verified'       => true,
                'is_published'      => true,
                'test_cases'        => [
                    ['input' => [12, 8],   'expected_output' => '4',  'is_hidden' => false],
                    ['input' => [15, 5],   'expected_output' => '5',  'is_hidden' => false],
                    ['input' => [9, 6],    'expected_output' => '3',  'is_hidden' => false],
                    ['input' => [7, 13],   'expected_output' => '1',  'is_hidden' => true],
                    ['input' => [100, 75], 'expected_output' => '25', 'is_hidden' => true],
                ],
            ],

            [
                'title'             => 'Suma de Dígitos',
                'short_description' => 'Suma todos los dígitos de un número entero.',
                'description'       => "Dado un entero no negativo `n`, devuelve la suma de todos sus dígitos.\n\n**Ejemplo:**\n```\nEntrada:  n = 123\nSalida:   6\n\nEntrada:  n = 456\nSalida:   15\n```",
                'difficulty'        => 'easy',
                'category'          => 'math',
                'function_name'     => 'sumaDigitos',
                'parameters'        => [['name' => 'n', 'type' => 'int']],
                'return_type'       => 'int',
                'is_verified'       => true,
                'is_published'      => true,
                'test_cases'        => [
                    ['input' => [123],  'expected_output' => '6',  'is_hidden' => false],
                    ['input' => [456],  'expected_output' => '15', 'is_hidden' => false],
                    ['input' => [0],    'expected_output' => '0',  'is_hidden' => false],
                    ['input' => [9999], 'expected_output' => '36', 'is_hidden' => true],
                    ['input' => [100],  'expected_output' => '1',  'is_hidden' => true],
                ],
            ],

            // ── CADENAS ───────────────────────────────────────────────────────

            [
                'title'             => '¿Es Palíndromo?',
                'short_description' => 'Comprueba si una cadena se lee igual al derecho y al revés.',
                'description'       => "Dada una cadena `s`, devuelve `true` si es palíndromo o `false` en caso contrario.\n\nUna cadena es palíndromo si se lee igual de izquierda a derecha que de derecha a izquierda.\n\n**Ejemplo:**\n```\nEntrada:  s = \"racecar\"\nSalida:   true\n\nEntrada:  s = \"hola\"\nSalida:   false\n```",
                'difficulty'        => 'easy',
                'category'          => 'strings',
                'function_name'     => 'esPalindromo',
                'parameters'        => [['name' => 's', 'type' => 'string']],
                'return_type'       => 'bool',
                'is_verified'       => true,
                'is_published'      => true,
                'test_cases'        => [
                    ['input' => ['racecar'], 'expected_output' => 'true',  'is_hidden' => false],
                    ['input' => ['hola'],    'expected_output' => 'false', 'is_hidden' => false],
                    ['input' => ['aba'],     'expected_output' => 'true',  'is_hidden' => false],
                    ['input' => ['a'],       'expected_output' => 'true',  'is_hidden' => true],
                    ['input' => ['abcba'],   'expected_output' => 'true',  'is_hidden' => true],
                    ['input' => ['hello'],   'expected_output' => 'false', 'is_hidden' => true],
                ],
            ],

            [
                'title'             => 'Contar Vocales',
                'short_description' => 'Cuenta el número de vocales en una cadena de texto.',
                'description'       => "Dada una cadena `s`, devuelve el número de vocales que contiene (`a, e, i, o, u`), sin distinguir mayúsculas de minúsculas.\n\n**Ejemplo:**\n```\nEntrada:  s = \"hola\"\nSalida:   2\n```",
                'difficulty'        => 'easy',
                'category'          => 'strings',
                'function_name'     => 'contarVocales',
                'parameters'        => [['name' => 's', 'type' => 'string']],
                'return_type'       => 'int',
                'is_verified'       => true,
                'is_published'      => true,
                'test_cases'        => [
                    ['input' => ['hola'],         'expected_output' => '2', 'is_hidden' => false],
                    ['input' => ['python'],        'expected_output' => '1', 'is_hidden' => false],
                    ['input' => ['aeiou'],         'expected_output' => '5', 'is_hidden' => false],
                    ['input' => ['xyz'],           'expected_output' => '0', 'is_hidden' => true],
                    ['input' => ['programacion'],  'expected_output' => '5', 'is_hidden' => true],
                ],
            ],

            [
                'title'             => 'Contar Palabras',
                'short_description' => 'Cuenta el número de palabras en una cadena.',
                'description'       => "Dada una cadena de texto `s`, devuelve el número de palabras que contiene. Las palabras están separadas por espacios simples. Si la cadena está vacía, devuelve `0`.\n\n**Ejemplo:**\n```\nEntrada:  s = \"hola mundo\"\nSalida:   2\n```",
                'difficulty'        => 'easy',
                'category'          => 'strings',
                'function_name'     => 'contarPalabras',
                'parameters'        => [['name' => 's', 'type' => 'string']],
                'return_type'       => 'int',
                'is_verified'       => true,
                'is_published'      => true,
                'test_cases'        => [
                    ['input' => ['hola mundo'],   'expected_output' => '2', 'is_hidden' => false],
                    ['input' => ['uno dos tres'],  'expected_output' => '3', 'is_hidden' => false],
                    ['input' => ['hola'],          'expected_output' => '1', 'is_hidden' => false],
                    ['input' => [''],              'expected_output' => '0', 'is_hidden' => true],
                    ['input' => ['a b c d e'],     'expected_output' => '5', 'is_hidden' => true],
                ],
            ],

            [
                'title'             => 'Primera Letra en Mayúscula',
                'short_description' => 'Convierte la primera letra de una cadena a mayúscula.',
                'description'       => "Dada una cadena `s`, devuelve la misma cadena con la primera letra en mayúscula y el resto sin modificar. Si la cadena está vacía, devuelve la cadena vacía.\n\n**Ejemplo:**\n```\nEntrada:  s = \"hola\"\nSalida:   \"Hola\"\n```",
                'difficulty'        => 'easy',
                'category'          => 'strings',
                'function_name'     => 'mayusculaInicial',
                'parameters'        => [['name' => 's', 'type' => 'string']],
                'return_type'       => 'string',
                'is_verified'       => true,
                'is_published'      => true,
                'test_cases'        => [
                    ['input' => ['hola'],   'expected_output' => 'Hola',   'is_hidden' => false],
                    ['input' => ['mundo'],  'expected_output' => 'Mundo',  'is_hidden' => false],
                    ['input' => ['python'], 'expected_output' => 'Python', 'is_hidden' => false],
                    ['input' => [''],       'expected_output' => '',       'is_hidden' => true],
                    ['input' => ['jAVA'],   'expected_output' => 'JAVA',   'is_hidden' => true],
                ],
            ],

            // ── LÓGICA / OTROS ────────────────────────────────────────────────

            [
                'title'             => 'FizzBuzz',
                'short_description' => 'Devuelve Fizz, Buzz, FizzBuzz o el número según las reglas clásicas.',
                'description'       => "Dado un entero `n`, devuelve:\n- `\"FizzBuzz\"` si es divisible por 3 **y** por 5\n- `\"Fizz\"` si solo es divisible por 3\n- `\"Buzz\"` si solo es divisible por 5\n- El número como string en cualquier otro caso\n\n**Ejemplo:**\n```\nEntrada:  n = 15  →  \"FizzBuzz\"\nEntrada:  n = 9   →  \"Fizz\"\nEntrada:  n = 7   →  \"7\"\n```",
                'difficulty'        => 'easy',
                'category'          => 'other',
                'function_name'     => 'fizzBuzz',
                'parameters'        => [['name' => 'n', 'type' => 'int']],
                'return_type'       => 'string',
                'is_verified'       => true,
                'is_published'      => true,
                'test_cases'        => [
                    ['input' => [1],  'expected_output' => '1',        'is_hidden' => false],
                    ['input' => [3],  'expected_output' => 'Fizz',     'is_hidden' => false],
                    ['input' => [5],  'expected_output' => 'Buzz',     'is_hidden' => false],
                    ['input' => [15], 'expected_output' => 'FizzBuzz', 'is_hidden' => false],
                    ['input' => [7],  'expected_output' => '7',        'is_hidden' => true],
                    ['input' => [30], 'expected_output' => 'FizzBuzz', 'is_hidden' => true],
                ],
            ],

            [
                'title'             => 'Clasificar Triángulo',
                'short_description' => 'Clasifica un triángulo en equilátero, isósceles o escaleno.',
                'description'       => "Dados tres enteros `a`, `b` y `c` que representan los lados de un triángulo, devuelve:\n- `\"equilátero\"` si los tres lados son iguales\n- `\"isósceles\"` si exactamente dos lados son iguales\n- `\"escaleno\"` si los tres lados son distintos\n\nPuedes asumir que los valores forman un triángulo válido.\n\n**Ejemplo:**\n```\nEntrada:  a = 3, b = 3, c = 3  →  \"equilátero\"\nEntrada:  a = 3, b = 3, c = 5  →  \"isósceles\"\n```",
                'difficulty'        => 'medium',
                'category'          => 'other',
                'function_name'     => 'clasificarTriangulo',
                'parameters'        => [
                    ['name' => 'a', 'type' => 'int'],
                    ['name' => 'b', 'type' => 'int'],
                    ['name' => 'c', 'type' => 'int'],
                ],
                'return_type'       => 'string',
                'is_verified'       => true,
                'is_published'      => true,
                'test_cases'        => [
                    ['input' => [3, 3, 3], 'expected_output' => 'equilátero', 'is_hidden' => false],
                    ['input' => [3, 3, 5], 'expected_output' => 'isósceles',  'is_hidden' => false],
                    ['input' => [3, 4, 5], 'expected_output' => 'escaleno',   'is_hidden' => false],
                    ['input' => [5, 5, 5], 'expected_output' => 'equilátero', 'is_hidden' => true],
                    ['input' => [4, 4, 7], 'expected_output' => 'isósceles',  'is_hidden' => true],
                    ['input' => [2, 5, 7], 'expected_output' => 'escaleno',   'is_hidden' => true],
                ],
            ],

            [
                'title'             => 'Calcular IMC',
                'short_description' => 'Clasifica el índice de masa corporal según peso y altura.',
                'description'       => "Dados el `peso` en kg y la `altura` en metros, calcula el IMC y devuelve la categoría:\n\n- IMC < 18.5 → `\"bajo peso\"`\n- 18.5 ≤ IMC < 25 → `\"normal\"`\n- 25 ≤ IMC < 30 → `\"sobrepeso\"`\n- IMC ≥ 30 → `\"obesidad\"`\n\nFórmula: `IMC = peso / (altura × altura)`\n\n**Ejemplo:**\n```\nEntrada:  peso = 70.0, altura = 1.75\nSalida:   \"normal\"\n```",
                'difficulty'        => 'easy',
                'category'          => 'other',
                'function_name'     => 'calcularIMC',
                'parameters'        => [
                    ['name' => 'peso',   'type' => 'float'],
                    ['name' => 'altura', 'type' => 'float'],
                ],
                'return_type'       => 'string',
                'is_verified'       => true,
                'is_published'      => true,
                'test_cases'        => [
                    ['input' => [50.0, 1.70],  'expected_output' => 'bajo peso', 'is_hidden' => false],
                    ['input' => [70.0, 1.75],  'expected_output' => 'normal',    'is_hidden' => false],
                    ['input' => [85.0, 1.70],  'expected_output' => 'sobrepeso', 'is_hidden' => false],
                    ['input' => [100.0, 1.70], 'expected_output' => 'obesidad',  'is_hidden' => true],
                    ['input' => [60.0, 1.80],  'expected_output' => 'normal',    'is_hidden' => true],
                ],
            ],

            // ── RECURSIÓN ─────────────────────────────────────────────────────

            [
                'title'             => 'Potencia de un Número',
                'short_description' => 'Calcula base elevado a exp de forma recursiva.',
                'description'       => "Dados un entero `base` y un entero no negativo `exp`, devuelve `base` elevado a la potencia `exp`.\n\nImplementa la solución de forma **recursiva**.\n\n**Ejemplo:**\n```\nEntrada:  base = 2, exp = 3\nSalida:   8\n\nEntrada:  base = 5, exp = 0\nSalida:   1\n```",
                'difficulty'        => 'medium',
                'category'          => 'recursion',
                'function_name'     => 'potencia',
                'parameters'        => [
                    ['name' => 'base', 'type' => 'int'],
                    ['name' => 'exp',  'type' => 'int'],
                ],
                'return_type'       => 'int',
                'is_verified'       => true,
                'is_published'      => true,
                'test_cases'        => [
                    ['input' => [2, 3],  'expected_output' => '8',    'is_hidden' => false],
                    ['input' => [5, 2],  'expected_output' => '25',   'is_hidden' => false],
                    ['input' => [3, 0],  'expected_output' => '1',    'is_hidden' => false],
                    ['input' => [10, 3], 'expected_output' => '1000', 'is_hidden' => true],
                    ['input' => [2, 10], 'expected_output' => '1024', 'is_hidden' => true],
                ],
            ],

        ];

        foreach ($exercises as $data) {
            $testCases = $data['test_cases'];
            unset($data['test_cases']);

            $data['templates'] = $generator->generate(
                $data['function_name'],
                $data['parameters'],
                $data['return_type']
            );

            $exercise = Exercise::create(array_merge($data, ['user_id' => $teacher->id]));

            foreach ($testCases as $tc) {
                $exercise->testCases()->create($tc);
            }

            TranslateExerciseJob::dispatchSync($exercise, 'es');
        }
    }
}
