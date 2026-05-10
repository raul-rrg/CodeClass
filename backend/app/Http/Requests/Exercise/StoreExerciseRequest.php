<?php

namespace App\Http\Requests\Exercise;

use App\Enums\Difficulty;
use App\Enums\ProgrammingLanguage;
use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Validation\Rule;

class StoreExerciseRequest extends FormRequest
{
    /**
     * Solo los profesores pueden crear ejercicios, ya esta validado por el middleware
     */
    public function authorize(): bool
    {
        return true;
    }

    /**
     * Reglas de validacion para crear un ejercicio.
     */

    public function rules(): array
    {
        return [
            'title'             => ['required', 'string', 'max:255'],
            'description'       => ['required', 'string'],
            'short_description' => ['nullable', 'string', 'max:160'],
            'difficulty'        => ['required', Rule::enum(Difficulty::class)],
            'category'          => ['required', 'string', 'max:255'],

            // Template - Codigo base para el ejercicio
            // El nombre de la funcion debe ser valido para cada lenguaje, y
            //los parametros deben tener un nombre y tipo definido que sea valido
            'function_name'     => ['required', 'string', 'max:64', 'regex:/^[a-zA-Z_][a-zA-Z0-9_-]*$/'],
            'parameters'        => ['required', 'array', 'min:1'],
            'parameters.*.name' => ['required', 'string', 'max:32', 'regex:/^[a-zA-Z_][a-zA-Z0-9_-]*$/'],
            'parameters.*.type' => ['required', 'string', Rule::in(['int', 'float', 'string', 'bool', 'array'])],

            'return_type'       => ['required', 'string', Rule::in(['int', 'float', 'string', 'bool', 'array'])],
            'solution_code'     => ['nullable', 'string'],
            'solution_language' => ['nullable', Rule::enum(ProgrammingLanguage::class)],
            'time_limit'        => ['nullable', 'numeric', 'min:1', 'max:10'],
            'memory_limit'      => ['nullable', 'integer', 'min:32', 'max:512'],
            'is_published'      => ['sometimes', 'boolean'],

            // Validación de los casos de prueba, deben venir en un array con input, output esperado y si es oculto o no
            'test_cases'        => ['required', 'array', 'min:1'],
            'test_cases.*.input'           => ['required', 'array'],
            'test_cases.*.expected_output' => ['required', 'string'],
            'test_cases.*.is_hidden'       => ['required', 'boolean'],
        ];
    }

    // Validación adicional: si el ejercicio se publica, al menos un test case debe ser oculto
    // para que los alumnos no puedan copiar los inputs/outputs y hacer trampa
    public function withValidator($validator): void
    {
        $validator->after(function ($validator) {
            if (!$this->boolean('is_published')) {
                return;
            }

            $hasHidden = collect($this->input('test_cases', []))->contains('is_hidden', true);

            if (!$hasHidden) {
                $validator->errors()->add(
                    'test_cases',
                    'Debe haber al menos un caso de prueba oculto para publicar el ejercicio.'
                );
            }
        });
    }
}
