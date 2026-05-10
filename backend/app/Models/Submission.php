<?php

namespace App\Models;

use App\Enums\ProgrammingLanguage;
use App\Enums\SubmissionStatus;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;

class Submission extends Model
{
    /**
     * Campos permitidos para asignación masiva.
     */
    protected $fillable = [
        'user_id',
        'exercise_id',
        'code',
        'language',
        'status',
        'compile_output',
        'judge0_token',
        'max_execution_time',
        'max_memory',
    ];

    /**
     * Convierte los campos al tipo correcto en PHP.
     */
    protected function casts(): array
    {
        return [
            'language'           => ProgrammingLanguage::class,
            'status'             => SubmissionStatus::class,
            'max_execution_time' => 'float',
            'max_memory'         => 'integer',
        ];
    }

    // Una entrega pertenece a un alumno
    public function user(): BelongsTo
    {
        return $this->belongsTo(User::class);
    }

    // Una entrega pertenece a un ejercicio
    public function exercise(): BelongsTo
    {
        return $this->belongsTo(Exercise::class);
    }

    // Una entrega tiene muchos resultados
    public function submissionResults(): HasMany
    {
        return $this->hasMany(SubmissionResult::class);
    }
}
