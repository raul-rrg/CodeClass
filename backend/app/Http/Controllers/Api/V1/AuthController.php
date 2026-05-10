<?php

namespace App\Http\Controllers\Api\V1;

use App\Http\Controllers\Controller;
use App\Http\Requests\Auth\LoginRequest;
use App\Http\Requests\Auth\RegisterRequest;
use App\Models\User;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Storage;
use Illuminate\Validation\ValidationException;


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

    public function updateProfile(Request $request)
    {
        $data = $request->validate([
            'name'                  => ['sometimes', 'string', 'max:255'],
            'current_password'      => ['required_with:password', 'string'],
            'password'              => ['sometimes', 'string', 'min:8', 'confirmed'],
            'password_confirmation' => ['required_with:password', 'string'],
        ]);

        $user = $request->user();

        if ($request->has('password')) {
            if (!Hash::check($request->current_password, $user->password)) {
                throw ValidationException::withMessages([
                    'current_password' => ['La contraseña actual no es correcta.'],
                ]);
            }
        }

        if ($request->has('name'))     $user->name     = $data['name'];
        if ($request->has('password')) $user->password = $data['password'];

        $user->save();

        return response()->json($user);
    }

    public function updateAvatar(Request $request)
    {
        $request->validate([
            'avatar' => ['required', 'image', 'mimes:jpg,jpeg,png,webp', 'max:2048'],
        ]);

        $user = $request->user();

        if ($user->avatar) {
            Storage::disk('public')->delete($user->avatar);
        }

        $ext  = $request->file('avatar')->getClientOriginalExtension();
        $path = $request->file('avatar')->storeAs('avatars', "{$user->id}.{$ext}", 'public');

        $user->avatar = $path;
        $user->save();

        return response()->json($user);
    }
}

