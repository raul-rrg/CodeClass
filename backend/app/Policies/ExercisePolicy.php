<?php

namespace App\Policies;

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
        // Ejercicio publicado: cualquier usuario puede verlo
        if ($exercise->is_published) {
            return true;
        }

        // Ejercicio no publicado: solo el profesor que lo creó puede verlo
        return $user->role === 'teacher' && $user->id === $exercise->user_id;
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

    public function restore(User $user, Exercise $exercise): bool
    {
        return false;
    }

    public function forceDelete(User $user, Exercise $exercise): bool
    {
        return false;
    }
}
