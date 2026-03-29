<?php

use App\Http\Controllers\Api\V1\AuthController;
use App\Http\Controllers\Api\V1\ExerciseController;
use App\Http\Controllers\Api\V1\SubmissionController;
use Illuminate\Support\Facades\Route;

// Rutas de autenticacion — prefijo /api/v1
Route::prefix('v1')->group(function () {

    // Rutas publicas — no requieren token
    Route::post('/register', [AuthController::class, 'register']);
    Route::post('/login',    [AuthController::class, 'login']);

    // Rutas protegidas — requieren token Sanctum en el header
    // Authorization: Bearer TOKEN
    Route::middleware('auth:sanctum')->group(function () {
        Route::post('/logout', [AuthController::class, 'logout']);
        Route::get('/me',      [AuthController::class, 'me']);


        // Ejercicios — lectura para los alumnos y profesores
        Route::apiResource('exercises', ExerciseController::class)
            ->only(['index', 'show']);

        // Ejercicios — escritura solo para profesores
        Route::apiResource('exercises', ExerciseController::class)
            ->only(['store', 'update', 'destroy'])
            ->middleware('role:teacher');

        // Entregas — solo para alumnos
        Route::apiResource('submissions', SubmissionController::class)
            ->only(['store', 'index', 'show']);
    });
});
