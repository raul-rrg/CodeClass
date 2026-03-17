<?php

namespace App\Http\Requests\Auth;

use Illuminate\Foundation\Http\FormRequest;

class LoginRequest extends FormRequest
{
    /**
     * Cualquier usuario puede hacer login — ruta publica.
     */
    public function authorize(): bool
    {
        return true;
    }

    /**
     * Reglas de validacion del formulario de login.
     */
    public function rules(): array
    {
        return [
            'email'    => ['required', 'email', 'lowercase'],
            'password' => ['required', 'string'],
        ];
    }
}
