<?php

namespace App\Http\Controllers\Api\V1;

use App\Http\Controllers\Controller;
use App\Http\Requests\Exercise\StoreExerciseRequest;
use App\Http\Requests\Exercise\UpdateExerciseRequest;
use App\Http\Resources\ExerciseResource;
use App\Models\Exercise;
use Illuminate\Http\Request;

class ExerciseController extends Controller
{
    public function index(Request $request)
    {
        // Traemos solo los ejercicios visibles para el usuario autenticado, con su autor incluido para mostrar su nombre.

        // La relación 'user' se carga con solo los campos 'id' y 'name' para optimizar la consulta.
        // La visibilidad se maneja con un scope local en el modelo Exercise, que filtra según el rol del usuario.
        $exercises = Exercise::visibleTo($request->user())->with('user:id,name')->get();

        return ExerciseResource::collection($exercises);
    }

    public function store(StoreExerciseRequest $request)
    {
        $data = $request->validated();
        $data['user_id'] = $request->user()->id;

        // Los test cases van en su propia tabla, los separamos antes de crear el ejercicio
        // para que Eloquent no intente asignar ese campo al modelo Exercise
        $testCasesData = $data['test_cases'];
        unset($data['test_cases']);

        $exercise = Exercise::create($data);
        $exercise->testCases()->createMany($testCasesData);

        return response()->json($exercise->load('testCases'), 201);
    }

    // Route Model Binding: Laravel resuelve el modelo por PK automáticamente
    // y retorna 404 si no existe
    public function show(Request $request, Exercise $exercise)
    {
        // TODO: usar ExerciseResource para ocultar solution_code a los alumnos

        $this->authorize('view', $exercise);

        $user = $request->user();

        // El profesor que creó el ejercicio ve todos los test cases incluidos los ocultos.
        // Los alumnos y otros profesores solo ven los públicos, para que no puedan hacer trampa.
        $isOwner = $user->role === 'teacher' && $user->id === $exercise->user_id;

        $exercise->load([
            'testCases' => fn($q) => $isOwner ? $q : $q->where('is_hidden', false),
        ]);

        return response()->json($exercise, 200);
    }

    public function update(UpdateExerciseRequest $request, Exercise $exercise)
    {
        $this->authorize('update', $exercise);

        $data = $request->validated();

        // Si el profesor manda test cases, reemplazamos todos los existentes por los nuevos.
        // Si no los manda, los test cases actuales no se tocan.
        if (array_key_exists('test_cases', $data)) {
            $testCasesData = $data['test_cases'];
            unset($data['test_cases']);

            $exercise->testCases()->delete();
            $exercise->testCases()->createMany($testCasesData);
        }

        $exercise->update($data);

        return response()->json($exercise->load('testCases'), 200);
    }

    public function destroy(Exercise $exercise)
    {
        $this->authorize('delete', $exercise);

        // Los test cases se eliminan automáticamente por el cascadeOnDelete de la BD
        $exercise->delete();

        return response()->noContent();
    }
}
