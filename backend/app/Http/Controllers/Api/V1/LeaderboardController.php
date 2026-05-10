<?php

namespace App\Http\Controllers\Api\V1;

use App\Http\Controllers\Controller;
use Illuminate\Support\Facades\Cache;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Storage;


// Ranking global: suma puntos por ejercicio único resuelto (easy=20, medium=30, hard=40, insane=50).
// Subquery deduplica submissions → solo cuenta la primera accepted por (usuario, ejercicio).
// Resultado cacheado 5 min. Top 100 ordenado por puntuación total.


class LeaderboardController extends Controller
{
    public function index()
    {
        return Cache::remember('leaderboard.global', 300, function () {
            $uniqueSolved = $this->uniqueSolvedSubquery();

            // Consulta principal: sumar puntos por usuario, ordenar y limitar
            // JOIN con users para obtener nombre y avatar, GROUP BY por usuario, ORDER BY puntos totales desc
            // Nota: mergeBindings necesario para subquery con bindings (puntos por dificultad)

            $rows = DB::table(DB::raw("({$uniqueSolved->toSql()}) as us"))
                ->mergeBindings($uniqueSolved)
                ->join('users', 'users.id', '=', 'us.user_id')
                ->select(
                    'us.user_id',
                    'users.name',
                    'users.avatar',
                    DB::raw('COUNT(*) as solved'),
                    DB::raw('SUM(us.points) as total_points')
                )
                ->groupBy('us.user_id', 'users.name', 'users.avatar')
                ->orderByDesc('total_points')
                ->limit(100)
                ->get();

            return response()->json(['data' => $this->formatRows($rows)]);
        });
    }

    // Subquery: ejercicio único resuelto por usuario, con puntos por dificultad
    // JOIN con tabla exercises para obtener dificultad y calcular puntos
    private function uniqueSolvedSubquery()
    {
        return DB::table('submissions as s')
            ->join('exercises as e', 'e.id', '=', 's.exercise_id')
            ->where('s.status', 'accepted')
            ->select('s.user_id', 's.exercise_id', DB::raw(self::pointsCase()))
            ->groupBy('s.user_id', 's.exercise_id', 'e.difficulty');
    }

    // CASE SQL reutilizable en leaderboard global y de torneo
    public static function pointsCase(): string
    {
        return "CASE e.difficulty
            WHEN 'easy'   THEN 20
            WHEN 'medium' THEN 30
            WHEN 'hard'   THEN 40
            WHEN 'insane' THEN 50
            ELSE 0
        END as points";
    }


    // Formatea resultado para respuesta API: posición, user_id, name, avatar_url, solved, total_points
    private function formatRows(\Illuminate\Support\Collection $rows): \Illuminate\Support\Collection
    {
        return $rows->values()->map(fn($row, $i) => [
            'position'     => $i + 1,
            'user_id'      => $row->user_id,
            'name'         => $row->name,
            'avatar_url'   => $row->avatar ? Storage::disk('public')->url($row->avatar) : null,
            'solved'       => (int) $row->solved,
            'total_points' => (int) $row->total_points,
        ]);
    }
}
