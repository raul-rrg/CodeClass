<?php

namespace App\Http\Controllers\Api\V1;

use App\Enums\SubmissionStatus;
use App\Http\Controllers\Controller;
use App\Http\Requests\Submission\StoreSubmissionRequest;
use App\Models\Submission;
use Illuminate\Http\Request;

class SubmissionController extends Controller
{
    public function store(StoreSubmissionRequest $request)
    {
        // Crea la entrega en BD con status inicial pending
        $submission = Submission::create([
            'user_id'     => $request->user()->id,
            'exercise_id' => $request->validated('exercise_id'),
            'code'        => $request->validated('code'),
            'language'    => $request->validated('language'),
            'status'      => SubmissionStatus::Pending,
        ]);

        // TODO: crear Job EvaluateSubmission — envía el código a Judge0 y actualiza el status
        // EvaluateSubmission::dispatch($submission);

        return response()->json($submission, 202);
    }

    public function index(Request $request)
    {
        // Devuelve solo las entregas del alumno autenticado
        $submissions = Submission::where('user_id', $request->user()->id)->get();

        return response()->json($submissions, 200);
    }

    public function show(Submission $submission)
    {
        // Verifica que la submission pertenece al usuario autenticado
        $this->authorize('view', $submission);

        // Devuelve la entrega con los resultados de cada test case evaluado por Judge0
        return response()->json($submission->load('submissionResults'), 200);
    }
}
