<?php

namespace App\Models;

use App\Enums\ProgrammingLanguage;
use Illuminate\Database\Eloquent\Builder;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;
use App\Enums\Difficulty;
use Spatie\Translatable\HasTranslations;

class Exercise extends Model
{


    use HasTranslations;

    public array $translatable = ['title', 'description', 'short_description'];

    /**
     * Los campos que se pueden rellenar masivamente.
     * Protege contra Mass Assignment Attack.
     */


    protected $fillable = [
        'user_id',
        'title',
        'description',
        'short_description',
        'difficulty',        // easy | medium | hard | insane
        'category',          // arrays | strings | math | recursion | sorting | other
        'function_name',
        'parameters',
        'return_type',
        'templates',
        'solution_code',
        'solution_language', // python | java | javascript
        'is_verified',
        'is_published',
    ];


    protected function casts(): array
    {
        return [
            'is_verified'       => 'boolean',
            'is_published'      => 'boolean',
            'difficulty'        => Difficulty::class,
            'solution_language' => ProgrammingLanguage::class,
            'parameters'        => 'array',
            'templates'         => 'array',
        ];
    }


    public function scopeVisibleTo(Builder $query, ?User $user): Builder
    {
        // Sin sesión o alumno: solo ejercicios publicados
        if (!$user || $user->role !== 'teacher') {
            return $query->where('is_published', true);
        }

        // Profesor: sus propios ejercicios (publicados o no) + el resto publicados
        return $query->where('is_published', true)->orWhere('user_id', $user->id);
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
