<?php

namespace App\Services;

// Genera el template por lenguaje a partir de la firma de la función definida por el profesor
class TemplateGenerator
{
    // Valor por defecto del return según tipo de retorno y lenguaje
    private const DEFAULTS = [
        'int'    => ['python' => '0',     'javascript' => '0',     'java' => '0'],
        'float'  => ['python' => '0.0',   'javascript' => '0.0',   'java' => '0.0f'],
        'string' => ['python' => '""',    'javascript' => '""',    'java' => '""'],
        'bool'   => ['python' => 'False', 'javascript' => 'false', 'java' => 'false'],
        'array'  => ['python' => '[]',    'javascript' => '[]',    'java' => 'new ArrayList<>()'],
    ];

    // Equivalencia de tipos genéricos a tipos Java (Java es tipado estáticamente)
    private const JAVA_TYPES = [
        'int'    => 'int',
        'float'  => 'float',
        'string' => 'String',
        'bool'   => 'boolean',
        'array'  => 'ArrayList<Object>',
    ];


    public function generate(string $functionName, array $parameters, string $returnType): array
    {
        return [
            'python'     => $this->python($functionName, $parameters, $returnType),
            'javascript' => $this->javascript($functionName, $parameters, $returnType),
            'java'       => $this->java($functionName, $parameters, $returnType),
        ];
    }

    // Python no necesita tipos en la firma — solo nombre de parámetros
    private function python(string $functionName, array $parameters, string $returnType): string
    {
        $params  = implode(', ', array_column($parameters, 'name'));
        $default = self::DEFAULTS[$returnType]['python'];

        return "def {$functionName}({$params}):\n    return {$default}";
    }

    // JavaScript no necesita tipos en la firma — solo nombre de parámetros
    private function javascript(string $functionName, array $parameters, string $returnType): string
    {
        $params  = implode(', ', array_column($parameters, 'name'));
        $default = self::DEFAULTS[$returnType]['javascript'];

        return "function {$functionName}({$params}) {\n    return {$default};\n}";
    }

    // Java necesita tipos en la firma tanto en parámetros como en el retorno
    private function java(string $functionName, array $parameters, string $returnType): string
    {
        $javaReturnType = self::JAVA_TYPES[$returnType];
        $default        = self::DEFAULTS[$returnType]['java'];

        // Cada parámetro lleva su tipo Java delante: "int a, String b"
        $params = implode(', ', array_map(
            fn($p) => self::JAVA_TYPES[$p['type']] . ' ' . $p['name'],
            $parameters
        ));

        return "public static {$javaReturnType} {$functionName}({$params}) {\n    return {$default};\n}";
    }
}
