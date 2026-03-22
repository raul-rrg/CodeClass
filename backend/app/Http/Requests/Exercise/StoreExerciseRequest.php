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
            'solution_code'     => ['nullable', 'string'],
            'solution_language' => ['nullable', Rule::enum(ProgrammingLanguage::class)],
            'time_limit'        => ['nullable', 'numeric', 'min:1', 'max:10'],
            'memory_limit'      => ['nullable', 'integer', 'min:32', 'max:512'],
        ];
    }
}
