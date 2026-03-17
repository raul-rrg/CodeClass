<?php

use App\Http\Controllers\Api\V1\AuthController;
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
    });

});
