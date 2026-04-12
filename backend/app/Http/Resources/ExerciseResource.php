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
            'time_limit'   => $this->time_limit,
            'memory_limit' => $this->memory_limit,
            'author'       => $this->user?->name,
            'created_at'   => $this->created_at,
        ];
    }
}
