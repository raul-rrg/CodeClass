<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

class ForceJsonResponse
{
    /**
     * Fuerza que todas las peticiones a la API acepten JSON.
     * Evita que Laravel devuelva HTML en lugar de JSON cuando
     * el cliente no envia el header Accept: application/json.
     */
    public function handle(Request $request, Closure $next): Response
    {

        $request->headers->set('Accept', 'application/json');

        return $next($request);
    }
}
