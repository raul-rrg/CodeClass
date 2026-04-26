<?php

namespace App\Http\Controllers\Api\V1;

use App\Enums\SubmissionStatus;
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
        $submission = Submission::create([
            'user_id'     => $request->user()->id,
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
        // Consulta base: submissions del ejercicio solicitado.
        $query = $exercise->submissions();
        $currentUser = $request->user();
        $isTeacher = $currentUser->role === 'teacher';

        // Los alumnos solo pueden ver sus propios envios.
        if (!$isTeacher) {
            $query->where('user_id', $currentUser->id);
        }

        // El profesor tambien necesita datos del alumno para identificar cada envio.
        $relations = $isTeacher
            ? ['submissionResults', 'user']
            : ['submissionResults'];

        // Ordenamos por mas reciente primero para mostrar el historial actualizado.
        return response()->json($query->with($relations)->latest()->get());
    }


    public function show(Submission $submission)
    {
        // El profesor puede ver cualquier submission, el alumno solo los suyos (ver SubmissionPolicy).
        $this->authorize('view', $submission);

        return response()->json($submission->load('submissionResults'));
    }
}
