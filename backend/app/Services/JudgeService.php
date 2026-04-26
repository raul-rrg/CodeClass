<?php

namespace App\Services;

use App\Enums\ProgrammingLanguage;
use Illuminate\Support\Facades\Http;

class JudgeService
{
    const TIME_LIMIT   = 5;   // segundos
    const MEMORY_LIMIT = 256; // MB


    public function runTestCase(string $language, string $code, string $functionName, array $parameters, string $returnType, object $testCase): array
    {
        // Java necesita que el código del alumno quede dentro de una clase Main
        $fullCode = $language === 'java'
            ? $this->buildJavaCode($code, $functionName, $parameters, $returnType)
            : $code . "\n" . $this->buildWrapper($language, $functionName);

        $response = Http::post(config('services.judge0.url') . '/submissions?wait=true&base64_encoded=true', [
            'source_code'     => base64_encode($fullCode),
            'language_id'     => ProgrammingLanguage::from($language)->judge0Id(),
            'stdin'           => base64_encode(is_array($testCase->input) ? json_encode($testCase->input) : $testCase->input),
            'expected_output' => base64_encode($testCase->expected_output),
            'cpu_time_limit'  => self::TIME_LIMIT,
            'memory_limit'    => self::MEMORY_LIMIT * 1024, // Judge0 espera KB
        ]);

        if ($response->failed() || !isset($response['status'])) {
            throw new \RuntimeException('Judge0 error: ' . $response->body());
        }

        $statusId = $response['status']['id'];
        $stdout   = isset($response['stdout'])         ? base64_decode($response['stdout'])         : '';
        $stderr   = isset($response['stderr'])         ? base64_decode($response['stderr'])         : '';
        $compile  = isset($response['compile_output']) ? base64_decode($response['compile_output']) : '';

        // Presentation Error: Judge0 dice Wrong Answer pero el output coincide tras trim
        if ($statusId === 4 && trim($stdout) === trim($testCase->expected_output)) {
            $statusId = 'presentation_error';
            $passed   = false;
        } else {
            $passed = $statusId === 3;
        }

        $status = match (true) {
            $statusId === 3                     => 'accepted',
            $statusId === 'presentation_error'  => 'presentation_error',
            $statusId === 5                     => 'time_limit_exceeded',
            $statusId === 6                     => 'compile_error',
            is_int($statusId) && $statusId >= 7 => 'runtime_error',
            default                             => 'wrong_answer',
        };

        return [
            'passed'         => $passed,
            'output'         => $stdout,
            'error'          => $stderr,
            'compile_output' => $compile,
            'status'         => $status,
            'time'           => $response['time'] ?? null,
        ];
    }

    // Wrapper para Python y JavaScript: se concatena al final del código del alumno
    private function buildWrapper(string $language, string $functionName): string
    {
        return match ($language) {
            // Lee args desde stdin (JSON array), desempaqueta con ... y serializa el resultado a JSON
            'javascript' => <<<JS
            const __args = JSON.parse(require('fs').readFileSync('/dev/stdin', 'utf8'));
            const __result = {$functionName}(...__args);
            console.log(typeof __result === 'string' ? __result : JSON.stringify(__result));
            JS,

            // Lee args desde stdin (JSON array), desempaqueta con * y serializa el resultado a JSON
            'python' => <<<PY
            import json, sys
            __args = json.load(sys.stdin)
            __result = {$functionName}(*__args)
            print(__result if isinstance(__result, str) else json.dumps(__result))
            PY,

            default => '',
        };
    }

    // Para Java el código del alumno se inyecta dentro de la clase Main
    private function buildJavaCode(string $studentCode, string $functionName, array $parameters, string $returnType): string
    {
        $javaTypeMap = [
            'int'    => 'int',
            'float'  => 'double',
            'string' => 'String',
            'bool'   => 'boolean',
            'array'  => 'String', // se pasa como JSON string raw
        ];

        $parseCodeMap = [
            'int'    => 'Integer.parseInt(t[%d].trim())',
            'float'  => 'Double.parseDouble(t[%d].trim())',
            'string' => 't[%d].trim().replaceAll("^\\"|\\"$", "")',
            'bool'   => 'Boolean.parseBoolean(t[%d].trim())',
            'array'  => 't[%d].trim()',
        ];

        $parseLines = [];
        $callArgs   = [];

        foreach ($parameters as $i => $param) {
            $javaType     = $javaTypeMap[$param['type']];
            $parseCode    = sprintf($parseCodeMap[$param['type']], $i);
            $parseLines[] = "        {$javaType} {$param['name']} = {$parseCode};";
            $callArgs[]   = $param['name'];
        }

        $parseLinesStr  = implode("\n", $parseLines);
        $callArgsStr    = implode(', ', $callArgs);
        $javaReturnType = $javaTypeMap[$returnType] ?? 'Object';

        // Arrays se imprimen como JSON string; primitivos directamente con println
        $printResult = $returnType === 'array'
            ? 'System.out.println(java.util.Arrays.toString(__result));'
            : 'System.out.println(__result);';

        // splitTokens: split de JSON array respetando strings y arrays anidados
        $splitHelper = <<<'JAVA'
    static String[] splitTokens(String s) {
        List<String> r = new ArrayList<>();
        int d = 0;
        StringBuilder c = new StringBuilder();
        boolean q = false;
        for (char ch : s.toCharArray()) {
            if (ch == '"') q = !q;
            if (!q) {
                if (ch == '[' || ch == '{') d++;
                else if (ch == ']' || ch == '}') d--;
                else if (ch == ',' && d == 0) {
                    r.add(c.toString().trim());
                    c = new StringBuilder();
                    continue;
                }
            }
            c.append(ch);
        }
        if (c.length() > 0) r.add(c.toString().trim());
        return r.toArray(new String[0]);
    }
JAVA;

        return <<<JAVA
import java.util.*;
import java.io.*;

public class Main {
{$studentCode}
    public static void main(String[] args) throws Exception {
        String raw = new BufferedReader(new InputStreamReader(System.in)).readLine().trim();
        raw = raw.substring(1, raw.length() - 1).trim();
        String[] t = splitTokens(raw);
{$parseLinesStr}
        {$javaReturnType} __result = new Main().{$functionName}({$callArgsStr});
        {$printResult}
    }
{$splitHelper}
}
JAVA;
    }
}
