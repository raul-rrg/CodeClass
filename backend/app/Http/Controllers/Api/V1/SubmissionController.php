<?php

namespace App\Http\Controllers\Api\V1;

use App\Enums\SubmissionStatus;
use App\Enums\UserRole;
use App\Http\Controllers\Controller;
use App\Http\Requests\Submission\StoreSubmissionRequest;
use App\Jobs\EvaluateSubmission;
use App\Models\Exercise;
use App\Models\Submission;
use Illuminate\Http\Request;

class SubmissionController extends Controller
{


    public function store(StoreSubmissionRequest $request, Exercise $exercise)
    {
        $user = $request->user();

        $submission = Submission::create([
            'user_id'     => $user->id,
            'exercise_id' => $exercise->id,
            'code'        => $request->validated('code'),
            'language'    => $request->validated('language'),
            'status'      => SubmissionStatus::Pending,
        ]);

        // En segundo plano, evaluamos el submission contra todos los test cases (visibles y ocultos).
        EvaluateSubmission::dispatch($submission);

        // Devolvemos el submission recién creado con sus resultados vacíos (aún no evaluados).
        return response()->json($submission->refresh()->load('submissionResults'), 201);
    }


    public function index(Request $request, Exercise $exercise)
    {
        $currentUser = $request->user();
        $isTeacher   = $currentUser->role === UserRole::Teacher;
        $isOwner     = $isTeacher && $exercise->user_id === $currentUser->id;
        $isGlobal    = $request->boolean('global');

        // Solo el profesor que creó el ejercicio puede ver outputs de casos ocultos aprobados.
        $fullRelations    = ['submissionResults.testCase:id,is_hidden,input,expected_output', 'user:id,name,avatar'];
        $limitedRelations = ['submissionResults.testCase:id,is_hidden', 'user:id,name,avatar'];

        if ($isGlobal) {
            $submissions = $exercise->submissions()
                ->with($isOwner ? $fullRelations : $limitedRelations)
                ->latest()
                ->get();

            if (!$isOwner) {
                $isSolved = !$isTeacher && $exercise->submissions()
                    ->where('user_id', $currentUser->id)
                    ->where('status', SubmissionStatus::Accepted)
                    ->exists();

                $submissions = $submissions->map(function ($sub) use ($currentUser, $isTeacher, $isSolved) {
                    if ($sub->user_id !== $currentUser->id) {
                        if (!$isTeacher && !$isSolved) $sub->code = null;
                        // Alumnos nunca ven resultados de submissions ajenas.
                        if (!$isTeacher) $sub->setRelation('submissionResults', collect());
                        // Profesor no-owner sí ve resultados pero con casos ocultos masked.
                        if ($isTeacher) $this->maskHiddenPassedResults($sub);
                    } else {
                        $this->maskHiddenPassedResults($sub);
                    }
                    return $sub;
                });
            }

            return response()->json($submissions);
        }

        // Vista personal: cada usuario ve solo sus propios envíos.
        $submissions = $exercise->submissions()
            ->where('user_id', $currentUser->id)
            ->with($isOwner ? $fullRelations : $limitedRelations)
            ->latest()
            ->get();

        if (!$isOwner) {
            $submissions->each(fn($sub) => $this->maskHiddenPassedResults($sub));
        }

        return response()->json($submissions);
    }

    private function maskHiddenPassedResults($submission): void
    {
        foreach ($submission->submissionResults as $result) {
            if ($result->testCase?->is_hidden && $result->passed) {
                $result->output = null;
            }
        }
    }


    public function mySubmissions(Request $request)
    {
        $submissions = $request->user()
            ->submissions()
            ->with('exercise:id,title,difficulty')
            ->latest()
            ->get();

        return response()->json($submissions);
    }

    public function show(Submission $submission)
    {
        // El profesor puede ver cualquier submission, el alumno solo los suyos (ver SubmissionPolicy).
        $this->authorize('view', $submission);

        return response()->json($submission->load('submissionResults'));
    }
}
