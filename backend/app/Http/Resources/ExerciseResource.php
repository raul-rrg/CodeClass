<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class ExerciseResource extends JsonResource
{
    public function toArray(Request $request): array
    {
        return [
            'id'           => $this->id,
            'title'        => $this->title,
            'description'  => $this->description,
            'difficulty'   => $this->difficulty,
            'category'     => $this->category,
            'is_verified'  => $this->is_verified,
            'is_published' => $this->is_published,
            'author'       => $this->user?->name,
            // Indica si el usuario actual ya resolvio este ejercicio.
            'is_solved'    => (bool) ($this->is_solved ?? false),
            'created_at'   => $this->created_at,
        ];
    }
}
