<?php

namespace App\Http\Requests\Submission;

use App\Enums\ProgrammingLanguage;
use Illuminate\Foundation\Http\FormRequest;
use Illuminate\Validation\Rule;

class StoreSubmissionRequest extends FormRequest
{

    // Cualquier usuario autenticado puede enviar una entrega, validado por el middleware
    public function authorize(): bool
    {
        return true;
    }

    /**
     * Get the validation rules that apply to the request.
     *
     * @return array<string, \Illuminate\Contracts\Validation\ValidationRule|array<mixed>|string>
     */
    public function rules(): array
    {
        return [
            // TODO: añadir max cuando se defina el límite de caracteres por ejercicio
            'code'     => ['required', 'string'],
            'language' => ['required', Rule::enum(ProgrammingLanguage::class)],
        ];
    }
}
