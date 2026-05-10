<?php

namespace App\Policies;

use App\Enums\UserRole;
use App\Models\Submission;
use App\Models\User;

class SubmissionPolicy
{
    public function viewAny(User $user): bool
    {
        // Cualquier usuario autenticado puede listar sus submissions
        return true;
    }

    public function view(User $user, Submission $submission): bool
    {
        // Profesor puede ver cualquier submission; alumno solo las suyas
        return $user->role === UserRole::Teacher || $user->id === $submission->user_id;
    }

    public function create(User $user): bool
    {
        // Cualquier usuario autenticado puede crear una submission
        return true;
    }

    public function update(User $user, Submission $submission): bool
    {
        // Las submissions son inmutables — nadie puede editarlas
        return false;
    }

    public function delete(User $user, Submission $submission): bool
    {
        // Las submissions son inmutables — nadie puede borrarlas
        return false;
    }

    public function restore(User $user, Submission $submission): bool
    {
        return false;
    }

    public function forceDelete(User $user, Submission $submission): bool
    {
        return false;
    }
}
