<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\BelongsToMany;

class Tournament extends Model
{
    protected $fillable = [
        'user_id',
        'name',
        'description',
        'starts_at',
        'ends_at',
        'duration_minutes',
        'is_public',
        'join_code',
        'cover_url',
    ];

    protected $appends = ['status'];

    protected function casts(): array
    {
        return [
            'starts_at'  => 'datetime',
            'ends_at'    => 'datetime',
            'is_public'  => 'boolean',
        ];
    }

    public function getStatusAttribute(): string
    {
        $now = now();
        if ($now->lt($this->starts_at)) return 'upcoming';
        if ($now->gt($this->ends_at))   return 'finished';
        return 'active';
    }

    public function creator(): BelongsTo
    {
        return $this->belongsTo(User::class, 'user_id');
    }

    public function exercises(): BelongsToMany
    {
        return $this->belongsToMany(Exercise::class, 'tournament_exercises');
    }

    public function participants(): BelongsToMany
    {
        return $this->belongsToMany(User::class, 'tournament_participants', 'tournament_id', 'user_id')
                    ->withPivot('joined_at');
    }
}
