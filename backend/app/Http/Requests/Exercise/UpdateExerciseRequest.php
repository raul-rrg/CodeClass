<?php

namespace App\Http\Requests\Exercise;

use App\Enums\Difficulty;
use App\Enums\ProgrammingLanguage;
use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Validation\Rule;

class UpdateExerciseRequest extends FormRequest
{
    /**
     * Solo los profesores pueden actualizar ejercicios, validado por el middleware
     */
    public function authorize(): bool
    {
        return true;
    }

    /**
     * Reglas de validacion para actualizar un ejercicio.
     * Todos los campos son opcionales — solo se actualizan los que se envian.
     */
    public function rules(): array
    {
        return [
            'title'             => ['sometimes', 'string', 'max:255'],
            'description'       => ['sometimes', 'string'],
            'difficulty'        => ['sometimes', Rule::enum(Difficulty::class)],
            'category'          => ['sometimes', 'string', 'max:255'],
            'solution_code'     => ['sometimes', 'nullable', 'string'],
            'solution_language' => ['sometimes', 'nullable', Rule::enum(ProgrammingLanguage::class)],
            'time_limit'        => ['nullable', 'numeric', 'min:1', 'max:10'],
            'memory_limit'      => ['nullable', 'integer', 'min:32', 'max:512'],
            'is_published'      => ['sometimes', 'boolean'],
            'test_cases'        => ['sometimes', 'array', 'min:1'],
            'test_cases.*.input'           => ['required_with:test_cases', 'string'],
            'test_cases.*.expected_output' => ['required_with:test_cases', 'string'],
            'test_cases.*.is_hidden'       => ['required_with:test_cases', 'boolean'],
        ];
    }

    // Validación adicional: si el ejercicio se publica, debe tener al menos un test case oculto
    public function withValidator($validator): void
    {
        $validator->after(function ($validator) {
            if (!$this->boolean('is_published')) {
                return;
            }

            $exercise = $this->route('exercise');

            // Si vienen test cases en el payload usamos esos, si no miramos los que ya tiene en BD
            $testCases = $this->input('test_cases') ?? $exercise->testCases()->get()->toArray();

            if (empty($testCases)) {
                $validator->errors()->add('is_published', 'No puedes publicar un ejercicio sin casos de prueba.');
                return;
            }

            if (!collect($testCases)->contains('is_hidden', true)) {
                $validator->errors()->add('is_published', 'Debe haber al menos un caso de prueba oculto para publicar el ejercicio.');
            }
        });
    }
}
