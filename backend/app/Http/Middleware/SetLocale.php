<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;

// Setea el locale de Laravel según el header Accept-Language del cliente.
// Los modelos translatable (Exercise) sirven el idioma correcto automáticamente.
class SetLocale
{
    private const SUPPORTED = ['es', 'en'];

    public function handle(Request $request, Closure $next): mixed
    {
        // substr(..., 0, 2): normaliza 'en-US', 'es-419', etc. a solo 'en'/'es'
        $locale = substr($request->header('Accept-Language', config('app.locale')), 0, 2);

        if (in_array($locale, self::SUPPORTED)) {
            app()->setLocale($locale);
        }

        return $next($request);
    }
}
