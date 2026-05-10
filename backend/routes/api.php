<?php

use App\Http\Controllers\Api\V1\AuthController;
use App\Http\Controllers\Api\V1\ExerciseController;
use App\Http\Controllers\Api\V1\RunController;
use App\Http\Controllers\Api\V1\SubmissionController;
use Illuminate\Support\Facades\Route;

Route::prefix('v1')->group(function () {

    // Rutas públicas
    Route::post('/register', [AuthController::class, 'register']);
    Route::post('/login',    [AuthController::class, 'login']);
    Route::get('/exercises', [ExerciseController::class, 'index']);

    // Rutas protegidas
    Route::middleware('auth:sanctum')->group(function () {
        Route::get('/exercises/{exercise}', [ExerciseController::class, 'show']);
        Route::post('/logout',    [AuthController::class, 'logout']);
        Route::get('/users/me',   [AuthController::class, 'me']);

        // Ejercicios — escritura y recursos propios solo profesores
        Route::middleware('role:teacher')->group(function () {
            Route::post('/exercises',              [ExerciseController::class, 'store']);
            Route::put('/exercises/{exercise}',    [ExerciseController::class, 'update']);
            Route::delete('/exercises/{exercise}', [ExerciseController::class, 'destroy']);
            Route::get('/users/me/exercises',                    [ExerciseController::class, 'myExercises']);
            Route::get('/users/me/exercises/stats',              [ExerciseController::class, 'myExercisesStats']);
            Route::get('/exercises/{exercise}/detail-stats',     [ExerciseController::class, 'exerciseDetailStats']);
        });

        // Run — ejecutar sin guardar (solo test cases visibles)
        Route::post('/exercises/{exercise}/run', [RunController::class, 'run']);

        // Submissions — anidadas bajo exercise para store/index, plana para show
        Route::post('/exercises/{exercise}/submissions', [SubmissionController::class, 'store']);
        Route::get('/exercises/{exercise}/submissions',  [SubmissionController::class, 'index']);
        Route::get('/submissions/{submission}',          [SubmissionController::class, 'show']);
    });
});
