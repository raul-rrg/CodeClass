<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

class CheckRole
{
     /**
     * Verifica que el usuario autenticado tiene el rol requerido.
     * Si no tiene el rol devuelve 403 Forbidden.
     *
     * Uso en rutas: middleware(['auth:sanctum', 'role:teacher'])
     */
    public function handle(Request $request, Closure $next, string $role): Response
    {
        // Comprobar si el rol del usuario coincide con el requerido
        if ($request->user()->role->value !== $role) {
            return response()->json(['message' => 'Unauthorized'], 403);
        }

        // Rol correcto — dejar pasar la peticion
        return $next($request);
    }
}
