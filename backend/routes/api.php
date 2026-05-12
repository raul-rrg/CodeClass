<?php

use App\Http\Controllers\Api\V1\AuthController;
use App\Http\Controllers\Api\V1\ExerciseController;
use App\Http\Controllers\Api\V1\LeaderboardController;
use App\Http\Controllers\Api\V1\RunController;
use App\Http\Controllers\Api\V1\SubmissionController;
use App\Http\Controllers\Api\V1\TournamentController;
use Illuminate\Support\Facades\Route;

Route::prefix('v1')->group(function () {

    // Rutas públicas
    Route::post('/register', [AuthController::class, 'register']);
    Route::post('/login',    [AuthController::class, 'login']);
    Route::get('/exercises', [ExerciseController::class, 'index']);
    Route::get('/leaderboard', [LeaderboardController::class, 'index']);
    Route::get('/tournaments', [TournamentController::class, 'index']);
    Route::get('/tournaments/{tournament}', [TournamentController::class, 'show']);

    // Rutas protegidas
    Route::middleware('auth:sanctum')->group(function () {
        Route::get('/exercises/{exercise}', [ExerciseController::class, 'show']);
        Route::post('/logout',    [AuthController::class, 'logout']);
        Route::get('/users/me',          [AuthController::class, 'me']);
        Route::put('/users/me',          [AuthController::class, 'updateProfile']);
        Route::post('/users/me/avatar',  [AuthController::class, 'updateAvatar']);

        // Ejercicios y torneos — escritura solo profesores
        Route::middleware('role:teacher')->group(function () {
            Route::post('/exercises',              [ExerciseController::class, 'store']);
            Route::put('/exercises/{exercise}',    [ExerciseController::class, 'update']);
            Route::delete('/exercises/{exercise}', [ExerciseController::class, 'destroy']);
            Route::get('/users/me/exercises',                    [ExerciseController::class, 'myExercises']);
            Route::get('/users/me/exercises/stats',              [ExerciseController::class, 'myExercisesStats']);
            Route::get('/exercises/{exercise}/detail-stats',     [ExerciseController::class, 'exerciseDetailStats']);
            Route::post('/tournaments', [TournamentController::class, 'store']);
        });

        // Torneos — inscribirse + leaderboard (cualquier usuario autenticado)
        Route::get('/tournaments/code/{code}', [TournamentController::class, 'findByCode']);
        Route::post('/tournaments/{tournament}/join', [TournamentController::class, 'join']);
        Route::get('/tournaments/{tournament}/leaderboard', [LeaderboardController::class, 'tournament']);

        // Submissions propias — cualquier usuario autenticado
        Route::get('/users/me/submissions', [SubmissionController::class, 'mySubmissions']);

        // Run — ejecutar sin guardar (solo test cases visibles)
        Route::post('/exercises/{exercise}/run', [RunController::class, 'run']);

        // Submissions — anidadas bajo exercise para store/index, plana para show
        Route::post('/exercises/{exercise}/submissions', [SubmissionController::class, 'store']);
        Route::get('/exercises/{exercise}/submissions',  [SubmissionController::class, 'index']);
        Route::get('/submissions/{submission}',          [SubmissionController::class, 'show']);
    });
});
