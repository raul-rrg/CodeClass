<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;

class TestCase extends Model
{
    /**
     * Campos permitidos para asignación masiva.
     */
    protected $fillable = [
        'exercise_id',
        'input',
        'expected_output',
        'is_hidden',
    ];

    /**
     * Convierte los campos al tipo correcto en PHP.
     */
    protected $casts = [
        'is_hidden' => 'boolean',
    ];

    // Un caso de prueba pertenece a un ejercicio
    public function exercise(): BelongsTo
    {
        return $this->belongsTo(Exercise::class);
    }

    // Un caso de prueba tiene muchos resultados
    public function submissionResults(): HasMany
    {
        return $this->hasMany(SubmissionResult::class);
    }
}
