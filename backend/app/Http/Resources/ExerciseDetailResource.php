<?php

namespace App\Http\Resources;

use Illuminate\Http\Request;
use Illuminate\Http\Resources\Json\JsonResource;

class ExerciseDetailResource extends JsonResource
{
    public function toArray(Request $request): array
    {
        return [
            'id'           => $this->id,
            'title'        => $this->title,
            'description'  => $this->description,
            'difficulty'   => $this->difficulty,
            'category'     => $this->category,
            'is_verified'   => $this->is_verified,
            'function_name' => $this->function_name,
            'author'        => $this->user?->name,
            'test_cases'   => $this->testCases->map(fn($tc) => [
                'id'              => $tc->id,
                'input'           => $tc->input,
                'expected_output' => $tc->expected_output,
                'is_hidden'       => $tc->is_hidden,
            ]),
        ];
    }
}
