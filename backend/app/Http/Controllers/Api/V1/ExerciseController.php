<?php

namespace App\Http\Controllers\Api\V1;

use App\Enums\SubmissionStatus;
use App\Http\Controllers\Controller;
use App\Http\Requests\Exercise\StoreExerciseRequest;
use App\Http\Requests\Exercise\UpdateExerciseRequest;
use App\Http\Resources\ExerciseDetailResource;
use App\Http\Resources\ExerciseResource;
use App\Jobs\TranslateExerciseJob;
use App\Models\Exercise;
use App\Services\TemplateGenerator;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;

class ExerciseController extends Controller
{
    public function index(Request $request)
    {
        $user  = Auth::guard('sanctum')->user();
        $query = Exercise::visibleTo($user)->with('user:id,name,avatar');

        if ($request->filled('difficulty')) {
            $query->where('difficulty', $request->difficulty);
        }

        if ($request->filled('category')) {
            $query->where('category', $request->category);
        }

        if ($request->filled('search')) {
            $locale = app()->getLocale();
            $query->whereRaw("json_extract(title, '$.{$locale}') LIKE ?", ['%' . $request->search . '%']);
        }

        if ($user) {
            $query->withExists([
                'submissions as is_solved' => fn($q) => $q
                    ->where('user_id', $user->id)
                    ->where('status', 'accepted'),
            ]);

            if ($request->filled('solved')) {
                $isSolved = (bool) (int) $request->solved;
                if ($isSolved) {
                    $query->whereHas('submissions', fn($q) => $q
                        ->where('user_id', $user->id)->where('status', 'accepted'));
                } else {
                    $query->whereDoesntHave('submissions', fn($q) => $q
                        ->where('user_id', $user->id)->where('status', 'accepted'));
                }
            }
        }

        return ExerciseResource::collection($query->paginate(12));
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

    public function update(UpdateExerciseRequest $request, Exercise $exercise, TemplateGenerator $generator)
    {
        $this->authorize('update', $exercise);

        $data = $request->validated();

        if (array_key_exists('test_cases', $data)) {
            $testCasesData = $data['test_cases'];
            unset($data['test_cases']);
            $exercise->testCases()->delete();
            $exercise->testCases()->createMany($testCasesData);
        }

        // Regenerar templates si cambia la firma de la función.
        $signatureChanged = isset($data['function_name']) || isset($data['parameters']) || isset($data['return_type']);
        if ($signatureChanged) {
            $data['templates'] = $generator->generate(
                $data['function_name'] ?? $exercise->function_name,
                $data['parameters']    ?? $exercise->parameters,
                $data['return_type']   ?? $exercise->return_type,
            );
        }

        $exercise->update($data);

        // En segundo plano, actualizamos la traducción del ejercicio al otro idioma si cambian los campos de texto o la firma.
        TranslateExerciseJob::dispatch($exercise, app()->getLocale());

        return response()->json($exercise->load('testCases'), 200);
    }

    public function myExercises(Request $request)
    {
        $exercises = Exercise::where('user_id', $request->user()->id)
            ->with('user:id,name')
            ->get();

        return ExerciseResource::collection($exercises);
    }

    public function myExercisesStats(Request $request)
    {
        $exercises = Exercise::where('user_id', $request->user()->id)
            ->withCount('submissions')
            ->withCount(['submissions as accepted_count' => fn($q) => $q->where('status', SubmissionStatus::Accepted)])
            ->get();

        return response()->json($exercises->map(fn($e) => [
            'id'                => $e->id,
            'title'             => $e->title,
            'difficulty'        => $e->difficulty,
            'is_published'      => $e->is_published,
            'submissions_count' => $e->submissions_count,
            'accepted_count'    => $e->accepted_count,
            'created_at'        => $e->created_at,
        ]));
    }

    public function exerciseDetailStats(Request $request, Exercise $exercise)
    {
        if ($exercise->user_id !== $request->user()->id) {
            return response()->json(['message' => 'Forbidden'], 403);
        }

        $byDate = $exercise->submissions()
            ->selectRaw('DATE(created_at) as date, COUNT(*) as total, COUNT(DISTINCT user_id) as students')
            ->groupBy('date')
            ->orderBy('date')
            ->get();

        $uniqueStudents = $exercise->submissions()->distinct('user_id')->count('user_id');

        return response()->json([
            'unique_students'     => $uniqueStudents,
            'submissions_by_date' => $byDate,
        ]);
    }

    public function destroy(Exercise $exercise)
    {
        $this->authorize('delete', $exercise);

        // Los test cases se eliminan automáticamente por el cascadeOnDelete de la BD
        $exercise->delete();

        return response()->noContent();
    }
}
