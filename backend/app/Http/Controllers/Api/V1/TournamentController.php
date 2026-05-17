<?php

namespace App\Http\Controllers\Api\V1;

use App\Http\Controllers\Controller;
use App\Models\Exercise;
use App\Models\Submission;
use App\Models\Tournament;
use Carbon\Carbon;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Gate;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Str;

class TournamentController extends Controller
{
    public function index(Request $request)
    {

        // Miramos si hay un filtro de estado (status) en la query string para filtrar
        // torneos por su estado actual (upcoming, active, finished). Si no hay filtro, devolvemos todos.
        $status = $request->query('status');


        // Se obtiene la lista de torneos con su número de participantes y datos del creador.
        $tournaments = Tournament::withCount('participants')
            ->with('creator:id,name')
            ->latest('starts_at')
            ->get()
            ->when($status, fn($col) => $col->filter(fn($t) => $t->status === $status))
            ->values()
            ->map(fn($t) => $this->formatTournament($t));

        return response()->json(['data' => $tournaments]);
    }



    public function store(Request $request)
    {

        $data = $request->validate([
            'name'             => 'required|string|max:255',
            'description'      => 'nullable|string',
            'starts_at'        => 'required|date|after:now',
            'duration_minutes' => 'required|integer|min:1|max:480',
            'is_public'        => 'boolean',
            'exercise_ids'     => 'required|array|min:1',
            'exercise_ids.*'   => 'integer|exists:exercises,id',
            'cover_url'        => 'nullable|url|max:2048',
        ]);


        // Se deebe validar que los retos añadidos al torneo son ocultos y
        // creados por el profesor que lo está creando, no se pueden añadir retos públicos ni de otros profesores.
        $validCount = Exercise::whereIn('id', $data['exercise_ids'])
            ->where('user_id', $request->user()->id)
            ->where('is_published', false)
            ->count();

        if ($validCount !== count($data['exercise_ids'])) {
            return response()->json(['message' => 'Solo puedes añadir ejercicios ocultos creados por ti.'], 422);
        }

        $isPublic = $data['is_public'] ?? true;

        $tournament = Tournament::create([
            'user_id'          => $request->user()->id,
            'name'             => $data['name'],
            'description'      => $data['description'] ?? null,
            'starts_at'        => $data['starts_at'],
            // El campo ends_at se calcula automáticamente sumando duration_minutes a starts_at, no se recibe en la request.
            'ends_at'          => Carbon::parse($data['starts_at'])->addMinutes($data['duration_minutes']),
            'duration_minutes' => $data['duration_minutes'],
            'is_public'        => $isPublic,
            // Si el torneo es privado, se genera un código de acceso aleatorio de 8 caracteres. Si es público, no se asigna código.
            'join_code'        => $isPublic ? null : strtoupper(Str::random(8)),
            'cover_url'        => $data['cover_url'] ?? null,
        ]);

        $tournament->exercises()->sync($data['exercise_ids']);

        return response()->json(['data' => $this->formatTournament($tournament->load('creator'))], 201);
    }

    public function show(Tournament $tournament)
    {

        // Cargamos número de participantes y datos del creador para la vista detallada de torneo.
        $tournament->loadCount('participants')->load('creator:id,name');

        // Verificamos permisos para mostrar detalles del torneo, especialmente la lista de ejercicios.
        $user          = auth('sanctum')->user();
        $isParticipant = $user ? $tournament->participants()->where('user_id', $user->id)->exists() : false;
        $isCreator     = $user && $user->id === $tournament->user_id;


        // Datos básicos del torneo + flags de participación y código de acceso
        $data = array_merge($this->formatTournament($tournament), [
            'is_participant' => $isParticipant,
            'join_code'      => $isCreator ? $tournament->join_code : null, // Solo el creador puede ver el código de acceso
        ]);


        // Solo los participantes inscritos o el creador pueden ver la lista de ejercicios y su estado resuelto/no resuelto.
        if ($user && Gate::forUser($user)->allows('viewExercises', $tournament)) {
            $solvedIds = $this->getSolvedExerciseIds($user, $tournament);

            $data['exercises'] = $tournament->exercises()
                ->select('exercises.id', 'exercises.title', 'exercises.difficulty', 'exercises.short_description')
                ->get()
                ->map(fn($e) => [
                    'id'                => $e->id,
                    'title'             => $e->title,
                    'difficulty'        => $e->difficulty,
                    'short_description' => $e->short_description,
                    'is_solved'         => in_array($e->id, $solvedIds),
                ]);
        }

        return response()->json(['data' => $data]);
    }


    // Buscar torneo por código de acceso (solo para unirse)
    public function findByCode(string $code)
    {
        $tournament = Tournament::where('join_code', strtoupper($code))->firstOrFail();
        return response()->json(['data' => ['id' => $tournament->id, 'name' => $tournament->name]]);
    }


    // Inscribirse en torneo (si es público o si se proporciona código correcto)
    public function join(Request $request, Tournament $tournament)
    {
        $this->authorize('join', $tournament);


        // Si no es publico validamos que el codigo de acceso sea corecto
        if (!$tournament->is_public) {
            $request->validate(['join_code' => 'required|string']);
            if (strtoupper($request->join_code) !== $tournament->join_code) {
                return response()->json(['message' => 'Código de acceso incorrecto.'], 403);
            }
        }

        $tournament->participants()->attach($request->user()->id, ['joined_at' => now()]);

        return response()->json(['message' => 'Inscrito correctamente.']);
    }



    // IDs de ejercicios resueltos por el usuario en el torneo — usado en show() para marcar is_solved por ejercicio
    private function getSolvedExerciseIds(?object $user, Tournament $tournament): array
    {
        if (!$user) return [];

        return Submission::where('user_id', $user->id)
            ->where('status', 'accepted')
            ->whereIn('exercise_id', $tournament->exercises()->pluck('exercises.id'))
            ->whereBetween('created_at', [$tournament->starts_at, $tournament->ends_at])
            ->pluck('exercise_id')
            ->toArray();
    }


    // Formatear torneo para respuesta API, incluyendo datos del creador y conteo de participantes.
    private function formatTournament(Tournament $t): array
    {
        return [
            'id'                 => $t->id,
            'name'               => $t->name,
            'description'        => $t->description,
            'starts_at'          => $t->starts_at,
            'ends_at'            => $t->ends_at,
            'duration_minutes'   => $t->duration_minutes,
            'status'             => $t->status,
            'is_public'          => $t->is_public,
            'cover_url'          => $t->cover_url,
            'participants_count' => $t->participants_count ?? 0,
            'creator'            => $t->creator ? [
                'id'         => $t->creator->id,
                'name'       => $t->creator->name,
                'avatar_url' => $t->creator->avatar
                    ? Storage::disk('public')->url($t->creator->avatar)
                    : null,
            ] : null,
        ];
    }
}
