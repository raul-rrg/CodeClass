<?php

namespace App\Http\Resources;

use App\Enums\UserRole;
use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class ExerciseDetailResource extends JsonResource
{
    public function toArray(Request $request): array
    {
        $currentUser = $request->user();

        // Solo el profesor autor del ejercicio puede ver casos ocultos
        $isOwner = $currentUser?->role === UserRole::Teacher && $currentUser?->id === $this->user_id;

        return [
            'id'                => $this->id,
            'title'             => $this->title,
            'short_description' => $this->short_description,
            'description'       => $this->description,
            'difficulty'        => $this->difficulty,
            'category'          => $this->category,
            'is_published'      => $this->is_published,
            'is_verified'       => $this->is_verified,
            'function_name'     => $this->function_name,
            'parameters'    => $this->parameters,
            'return_type'   => $this->return_type,
            'templates'     => $this->templates,
            'author'        => $this->user?->name,
            'is_solved'     => $currentUser
                ? $this->submissions()->where('user_id', $currentUser->id)->where('status', 'accepted')->exists()
                : false,
            // Transformamos cada caso de prueba al formato de salida de la API.
            'test_cases'    => $this->testCases->map(function ($testCase) use ($isOwner) {

                // Si el caso no está oculto, o es el autor, se revelan entrada/salida esperada
                $canRevealCaseData = !$testCase->is_hidden || $isOwner;

                return [
                    'id'              => $testCase->id,
                    'is_hidden'       => $testCase->is_hidden,
                    // Para los alumnos y profesores sin permiso devolvemos null y evitamos exponer la solución
                    'input'           => $canRevealCaseData ? $testCase->input : null,
                    'expected_output' => $canRevealCaseData ? $testCase->expected_output : null,
                ];
            }),
        ];
    }
}
