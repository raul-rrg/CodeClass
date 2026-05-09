<?php

namespace App\Http\Controllers\Api\V1;

use App\Http\Controllers\Controller;
use App\Http\Requests\Exercise\StoreExerciseRequest;
use App\Http\Requests\Exercise\UpdateExerciseRequest;
use App\Http\Resources\ExerciseResource;
use App\Http\Resources\ExerciseDetailResource;
use App\Jobs\TranslateExerciseJob;
use App\Models\Exercise;
use App\Services\TemplateGenerator;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class ExerciseController extends Controller
{
    public function index(Request $request)
    {
        // Obtenemos el usuario con Sanctum
        $user = Auth::guard('sanctum')->user();

        // Consulta base: ejercicios visibles para el usuario actual + autor (solo id y name para optimizar).
        // Si no hay usuario autenticado, visibleTo normalmente devuelve solo ejercicios publicados.
        $query = Exercise::visibleTo($user)->with('user:id,name');

        if ($request->filled('search')) {
            $locale = app()->getLocale();
            $query->whereRaw("json_extract(title, '$.{$locale}') LIKE ?", ['%' . $request->search . '%']);
        }

        if ($user) {
            // Anadimos una columna booleana calculada (is_solved) por cada ejercicio.
            // withExists verifica si existe al menos una submission aceptada del usuario actual.
            // Importante: NO se guarda en la tabla exercises; es un alias temporal de la query
            // y luego se devuelve en el JSON a traves de ExerciseResource.
            $query->withExists([
                'submissions as is_solved' => fn($submissionQuery) => $submissionQuery
                    ->where('user_id', $user->id)
                    ->where('status', 'accepted'),
            ]);
        }

        // Ejecutamos la consulta y serializamos el resultado con ExerciseResource.
        return ExerciseResource::collection($query->get());
    }

    public function store(StoreExerciseRequest $request, TemplateGenerator $generator)
    {
        $data = $request->validated();
        $data['user_id'] = $request->user()->id;

        // Generamos el template por lenguaje a partir de la firma definida por el profesor
        $data['templates'] = $generator->generate(
            $data['function_name'],
            $data['parameters'],
            $data['return_type']
        );

        // Los test cases van en su propia tabla, los separamos antes de crear el ejercicio
        // para que Eloquent no intente asignar ese campo al modelo Exercise
        $testCasesData = $data['test_cases'];
        unset($data['test_cases']);

        $exercise = Exercise::create($data);
        $exercise->testCases()->createMany($testCasesData);

        // En segundo plano, traducimos el ejercicio al otro idioma (si se creó en español, lo traducimos al inglés y viceversa).
        TranslateExerciseJob::dispatch($exercise, app()->getLocale());

        return response()->json($exercise->load('testCases'), 201);
    }

    // Route Model Binding: Laravel resuelve el modelo por PK automáticamente
    // y retorna 404 si no existe
    public function show(Request $request, Exercise $exercise)
    {
        $this->authorize('view', $exercise);

        $user = $request->user();

        // El profesor que creó el ejercicio ve todos los test cases incluidos los ocultos.
        // Los alumnos y otros profesores solo ven los públicos, para que no puedan hacer trampa.
        $exercise->load(['user:id,name', 'testCases']);

        return new ExerciseDetailResource($exercise);
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

        // En segundo plano, actualizamos la traducción del ejercicio al otro idioma si cambian los campos de texto o la firma.
        TranslateExerciseJob::dispatch($exercise, app()->getLocale());

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
