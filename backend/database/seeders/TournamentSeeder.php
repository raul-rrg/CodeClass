<?php

namespace Database\Seeders;

use App\Models\Exercise;
use App\Models\Submission;
use App\Models\Tournament;
use App\Models\User;
use Illuminate\Database\Seeder;

class TournamentSeeder extends Seeder
{
    public function run(): void
    {
        $teacher  = User::where('role', 'teacher')->first();
        // Solo alumnos demo — excluir la cuenta student@example.com para no marcar sus ejercicios como resueltos
        $students = User::where('role', 'student')->where('email', 'like', '%@demo.com')->get();

        if (!$teacher) return;

        // Elimina torneos de seed con nombre en texto plano (migración a keys i18n)
        Tournament::whereIn('name', [
            'Torneo de Iniciación', 'Hackathon de Algoritmos', 'Torneo de Primavera',
        ])->delete();

        // Los 3 ejercicios fáciles del torneo (2 privados + 1 público)
        $tournamentExIds = Exercise::whereIn('function_name', ['sumar', 'esPar', 'invertir'])
            ->pluck('id')->toArray();

        // Ejercicios públicos para los otros torneos
        $mathIds    = Exercise::where('category', 'math')->where('is_published', true)->pluck('id')->toArray();
        $stringsIds = Exercise::where('category', 'strings')->where('is_published', true)->pluck('id')->toArray();
        $mixedIds   = Exercise::where('is_published', true)->pluck('id')->take(6)->toArray();

        $tournamentsData = [
            [
                'name'             => 'seed_tournaments.initiation.name',
                'description'      => 'seed_tournaments.initiation.description',
                'starts_at'        => now()->subHour(),
                'ends_at'          => now()->addHours(3),
                'duration_minutes' => 60,
                'is_public'        => true,
                'cover_url'        => 'https://images.unsplash.com/photo-1504639725590-34d0984388bd?w=800&q=80',
                'exercises'        => $tournamentExIds,
            ],
            [
                'name'             => 'seed_tournaments.algorithms.name',
                'description'      => 'seed_tournaments.algorithms.description',
                'starts_at'        => now()->addDay(),
                'ends_at'          => now()->addDay()->addHours(2),
                'duration_minutes' => 120,
                'is_public'        => true,
                'cover_url'        => 'https://images.unsplash.com/photo-1518770660439-4636190af475?w=800&q=80',
                'exercises'        => array_merge($mathIds, $stringsIds),
            ],
            [
                'name'             => 'seed_tournaments.spring.name',
                'description'      => 'seed_tournaments.spring.description',
                'starts_at'        => now()->subDays(3),
                'ends_at'          => now()->subDays(3)->addHours(2),
                'duration_minutes' => 120,
                'is_public'        => true,
                'cover_url'        => 'https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800&q=80',
                'exercises'        => $mixedIds,
            ],
        ];

        foreach ($tournamentsData as $data) {
            $exerciseIds = $data['exercises'];
            unset($data['exercises']);

            if (empty($exerciseIds)) continue;

            $tournament = Tournament::updateOrCreate(
                ['name' => $data['name']],
                array_merge($data, ['user_id' => $teacher->id])
            );

            $tournament->exercises()->syncWithoutDetaching($exerciseIds);

            // Torneo activo: inscribir alumnos y crear submissions de demo
            if ($tournament->status === 'active' && $students->isNotEmpty()) {
                foreach ($students->take(8) as $i => $student) {
                    if (!$tournament->participants()->where('user_id', $student->id)->exists()) {
                        $tournament->participants()->attach($student->id, ['joined_at' => $tournament->starts_at]);
                    }

                    $toSolve = array_slice($exerciseIds, 0, max(0, count($exerciseIds) - $i));

                    foreach ($toSolve as $exId) {
                        $exists = Submission::where('user_id', $student->id)
                            ->where('exercise_id', $exId)
                            ->where('status', 'accepted')
                            ->whereBetween('created_at', [$tournament->starts_at, $tournament->ends_at])
                            ->exists();

                        if (!$exists) {
                            Submission::create([
                                'user_id'     => $student->id,
                                'exercise_id' => $exId,
                                'code'        => '// solución demo',
                                'language'    => ['javascript', 'python', 'java'][rand(0, 2)],
                                'status'      => 'accepted',
                                'created_at'  => $tournament->starts_at->copy()->addMinutes(rand(3, 55)),
                                'updated_at'  => $tournament->starts_at->copy()->addMinutes(rand(3, 55)),
                            ]);
                        }
                    }
                }
            }

            // Torneo finalizado: participantes + submissions históricas
            if ($tournament->status === 'finished' && $students->isNotEmpty()) {
                foreach ($students->take(6) as $i => $student) {
                    if (!$tournament->participants()->where('user_id', $student->id)->exists()) {
                        $tournament->participants()->attach($student->id, ['joined_at' => $tournament->starts_at]);
                    }

                    $toSolve = array_slice($exerciseIds, 0, max(0, count($exerciseIds) - $i));

                    foreach ($toSolve as $exId) {
                        $exists = Submission::where('user_id', $student->id)
                            ->where('exercise_id', $exId)
                            ->where('status', 'accepted')
                            ->whereBetween('created_at', [$tournament->starts_at, $tournament->ends_at])
                            ->exists();

                        if (!$exists) {
                            Submission::create([
                                'user_id'     => $student->id,
                                'exercise_id' => $exId,
                                'code'        => '// solución demo',
                                'language'    => ['javascript', 'python', 'java'][rand(0, 2)],
                                'status'      => 'accepted',
                                'created_at'  => $tournament->starts_at->copy()->addMinutes(rand(5, 100)),
                                'updated_at'  => $tournament->starts_at->copy()->addMinutes(rand(5, 100)),
                            ]);
                        }
                    }
                }
            }
        }
    }
}
