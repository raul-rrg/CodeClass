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
        ];
    }
}
