<?php

namespace App\Policies;

use App\Enums\UserRole;
use App\Models\Tournament;
use App\Models\User;

class TournamentPolicy
{

    public function join(User $user, Tournament $tournament): bool
    {
        if ($tournament->status === 'finished') return false;
        if ($user->role === UserRole::Teacher)  return false;

        return !$tournament->participants()->where('user_id', $user->id)->exists();
    }


    public function viewExercises(?User $user, Tournament $tournament): bool
    {
        if (!$user) return false;
        if ($user->id === $tournament->user_id) return true;
        if ($tournament->status === 'upcoming') return false; // torneo aún no empezado

        return $tournament->participants()->where('user_id', $user->id)->exists();
    }

    public function viewLeaderboard(?User $user, Tournament $tournament): bool
    {
        if (!$user) return false;
        if ($user->id === $tournament->user_id) return true;

        return $tournament->participants()->where('user_id', $user->id)->exists();
    }
}
