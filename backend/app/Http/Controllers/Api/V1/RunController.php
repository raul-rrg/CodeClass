<?php

namespace App\Http\Controllers\Api\V1;

use App\Enums\ProgrammingLanguage;
use App\Http\Controllers\Controller;
use App\Models\Exercise;
use App\Services\JudgeService;
use Illuminate\Http\Request;
use Illuminate\Validation\Rule;

class RunController extends Controller
{
    /**
     * Ejecuta el código contra los test cases visibles sin guardar en BD.
     * Útil para depurar antes de hacer el envío oficial.
     */
    public function run(Request $request, Exercise $exercise, JudgeService $judge)
    {
        $request->validate([
            'code'     => ['required', 'string'],
            'language' => ['required', Rule::enum(ProgrammingLanguage::class)],
        ]);

        // Solo test cases visibles — los ocultos se reservan para el envío oficial
        $testCases = $exercise->testCases->where('is_hidden', false);

        $results       = [];
        $compileOutput = '';

        foreach ($testCases as $case) {
            $result = $judge->runTestCase(
                $request->language,
                $request->code,
                $exercise->function_name,
                $exercise->parameters,
                $exercise->return_type,
                $case
            );

            // Errores que afectan a todos los tests por igual: paramos al primero
            // y devolvemos un único mensaje con el tipo de error.
            //
            // - compile_error / runtime_error: el código no puede ejecutarse en absoluto.
            // - time_limit_exceeded: si el código entra en bucle infinito, todos los tests
            //   fallarían igual. Mostramos el tiempo del primer test que tardó demasiado.
            //
            // $result['compile_output'] tiene el error en lenguajes compilados (Java).
            // $result['error'] tiene el stderr en lenguajes interpretados (JS, Python).
            if (in_array($result['status'], ['compile_error', 'runtime_error', 'time_limit_exceeded'])) {
                return response()->json([
                    'passed_count'   => 0,
                    'total_count'    => $testCases->count(),
                    'error_type'     => $result['status'],
                    'time'           => $result['time'],
                    'compile_output' => $result['compile_output'] ?: $result['error'],
                    'results'        => [],
                ]);
            }

            // Wrong Answer y Presentation Error sí pueden variar por test case, seguimos.
            $results[] = [
                'passed'          => $result['passed'],
                'output'          => $result['output'],
                'error'           => $result['error'],
                'status'          => $result['status'],
                'time'            => $result['time'],
                'expected_output' => $case->expected_output,
            ];

            if (!$result['passed']) $allPassed = false;
            $compileOutput .= $result['compile_output'];
        }

        $passedCount = collect($results)->where('passed', true)->count();

        return response()->json([
            'passed_count'   => $passedCount,
            'total_count'    => count($results),
            'compile_output' => $compileOutput,
            'results'        => $results,
        ]);
    }
}
