<?php

namespace App\Policies;

use App\Enums\UserRole;
use App\Models\Exercise;
use App\Models\User;

class ExercisePolicy
{
    public function viewAny(User $user): bool
    {
        // Cualquier usuario autenticado puede ver la lista de ejercicios
        return true;
    }

    public function view(User $user, Exercise $exercise): bool
    {
        // Creador siempre puede ver su propio ejercicio
        if ($user->role === UserRole::Teacher && $user->id === $exercise->user_id) {
            return true;
        }

        $tournament = $exercise->tournaments()->first();

        if ($tournament) {
            if ($tournament->status === 'upcoming') return false;
            if ($tournament->status === 'active') {
                return $tournament->participants()->where('user_id', $user->id)->exists();
            }
        }

        // is_published controla solo el listado público (scopeVisibleTo), no el acceso directo
        return true;
    }

    public function create(User $user): bool
    {
        // El middleware role:teacher ya garantiza que solo profesores llegan aquí
        return true;
    }

    public function update(User $user, Exercise $exercise): bool
    {
        // Solo el profesor que creó el ejercicio puede editarlo
        return $user->id === $exercise->user_id;
    }

    public function delete(User $user, Exercise $exercise): bool
    {
        // Solo el profesor que creó el ejercicio puede borrarlo
        return $user->id === $exercise->user_id;
    }

}
