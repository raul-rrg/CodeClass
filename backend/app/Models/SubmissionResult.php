<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class SubmissionResult extends Model
{
    /**
     * SubmissionResult solo tiene created_at, no updated_at.
     * Un resultado nunca se modifica, solo se crea.
     */
    public $timestamps = false;

    /**
     * Campos permitidos para asignación masiva.
     */
    protected $fillable = [
        'submission_id',
        'test_case_id',
        'passed',
        'output',
        'error',
        'status',
        'execution_time',
        'memory',
    ];

    /**
     * Convierte los campos al tipo correcto en PHP.
     */
    protected function casts(): array
    {
        return [
            'passed'         => 'boolean',
            'execution_time' => 'float',
            'memory'         => 'integer',
        ];
    }

    // Un resultado pertenece a una entrega
    public function submission(): BelongsTo
    {
        return $this->belongsTo(Submission::class);
    }

    // Un resultado pertenece a un caso de prueba
    public function testCase(): BelongsTo
    {
        return $this->belongsTo(TestCase::class);
    }
}
