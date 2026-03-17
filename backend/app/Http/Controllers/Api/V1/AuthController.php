<?php

namespace App\Http\Controllers\Api\V1;

use App\Http\Controllers\Controller;
use App\Http\Requests\Auth\LoginRequest;
use App\Http\Requests\Auth\RegisterRequest;
use App\Models\User;
use Illuminate\Support\Facades\Auth;
use Illuminate\Http\Request;


class AuthController extends Controller
{


    // Validamos los datos con RegisterRequest — Inyeccion de dependcias
    public function register(RegisterRequest $request)
    {

        // Recogemos los datos del request y guardamos el usuario en la BD
        $user = User::create([
            'name' => $request->name,
            'email' => $request->email,
            'password' => $request->password,
            'role' => $request->role,
        ]);

        // Generamos el token de autenticacion
        $token = $user->createToken('auth_token')->plainTextToken;

        // Devolvemos el json con el usuario y el token - 201 Created Succes
        return response()->json([
            'user' => $user,
            'token' => $token,
        ], 201);
    }

    public function login(LoginRequest $request)
    {
        // Auth::attempt() — verifica credenciales con bcrypt
        if (!Auth::attempt(['email' => $request->email, 'password' => $request->password])) {
            // Credenciales incorrectas — 401 Unauthorized
            return response()->json(['message' => 'Invalid credentials'], 401);
        }

        // Credenciales correctas — generar token Sanctum
        $user  = Auth::user();
        $token = $user->createToken('auth_token')->plainTextToken;

        // Devolver usuario y token — 200 OK
        return response()->json([
            'user'  => $user,
            'token' => $token,
        ], 200);
    }

    public function logout(Request $request) {
        // Revoke the token that was used to authenticate the current request
        $request->user()->currentAccessToken()->delete();

        return response()->json(['message' => 'Logged out successfully'], 200);

    }

    public function me(Request $request) {
        return response()->json($request->user(), 200);
    }


}

