<?php

namespace App\Models;

use App\Enums\ProgrammingLanguage;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;
use App\Enums\Difficulty;

class Exercise extends Model
{


    /**
     * Los campos que se pueden rellenar masivamente.
     * Protege contra Mass Assignment Attack.
     */


    protected $fillable = [
        'user_id',
        'title',
        'description',
        'difficulty',        // easy | medium | hard | insane
        'category',          // arrays | strings | math | recursion | sorting | other
        'solution_code',
        'solution_language', // python | java | javascript
        'is_verified',
        'is_published',
        'time_limit',        // segundos
        'memory_limit',      // MB
    ];

    /**
     * Convierte los campos de la BD al tipo de dato correcto en PHP.
     */
    protected $casts = [
        'is_verified'  => 'boolean',
        'is_published' => 'boolean',
        'time_limit'   => 'float',
        'memory_limit' => 'integer',
    ];


    protected function casts(): array
    {
        return [
            'is_verified'  => 'boolean',
            'is_published' => 'boolean',
            'time_limit'   => 'float',
            'memory_limit' => 'integer',
            'difficulty' => Difficulty::class,
            'solution_languagee' => ProgrammingLanguage::class
        ];

    }


    // Un ejercicio pertenece a un profesor
    public function user(): BelongsTo
    {
        return $this->belongsTo(User::class);
    }

    // Un ejercicio tiene muchos casos de prueba
    public function testCases(): HasMany
    {
        return $this->hasMany(TestCase::class);
    }

    // Un ejercicio tiene muchas entregas
    public function submissions(): HasMany
    {
        return $this->hasMany(Submission::class);
    }
}
