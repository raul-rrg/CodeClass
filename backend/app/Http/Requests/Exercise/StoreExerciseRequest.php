<?php

namespace App\Http\Requests\Exercise;

use App\Enums\Difficulty;
use App\Enums\ProgrammingLanguage;
use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Validation\Rule;

class StoreExerciseRequest extends FormRequest
{
    /**
     * Solo los profesores pueden crear ejercicios, validado por el middleware
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
            'difficulty'        => ['required', Rule::enum(Difficulty::class)],
            'category'          => ['required', 'string', 'max:255'],
            // Code base — firma de la función que genera el template y el wrapper
            'function_name'     => ['required', 'string', 'max:64', 'regex:/^[a-zA-Z_][a-zA-Z0-9_]*$/'],
            'parameters'        => ['required', 'array', 'min:1'],
            'parameters.*.name' => ['required', 'string', 'max:32', 'regex:/^[a-zA-Z_][a-zA-Z0-9_]*$/'],
            'parameters.*.type' => ['required', 'string', Rule::in(['int', 'float', 'string', 'bool', 'array'])],
            'return_type'       => ['required', 'string', Rule::in(['int', 'float', 'string', 'bool', 'array'])],
            'solution_code'     => ['nullable', 'string'],
            'solution_language' => ['nullable', Rule::enum(ProgrammingLanguage::class)],
            'time_limit'        => ['nullable', 'numeric', 'min:1', 'max:10'],
            'memory_limit'      => ['nullable', 'integer', 'min:32', 'max:512'],
            'is_published'      => ['sometimes', 'boolean'],
            'test_cases'        => ['required', 'array', 'min:1'],
            'test_cases.*.input'           => ['required', 'string'],
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
