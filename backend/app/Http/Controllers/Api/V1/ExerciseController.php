<?php

namespace App\Http\Controllers\Api\V1;

use App\Http\Controllers\Controller;
use App\Http\Requests\Exercise\StoreExerciseRequest;
use App\Http\Requests\Exercise\UpdateExerciseRequest;
use App\Models\Exercise;

class ExerciseController extends Controller
{


    public function index()
    {
        // TODO: distinguir entre profesor y alumno con Service Layer
        // Profesor → sus propios ejercicios (publicados y no publicados)
        // Alumno → solo ejercicios publicados

        return response()->json(Exercise::all(), 200);
    }


    public function store(StoreExerciseRequest $request)
    {
        // Recogemos los campos validados del Form Request
        $data = $request->validated();

        // Asignamos el user_id desde el token
        $data['user_id'] = $request->user()->id;

        // Creamos el ejercicio en la BD
        $exercise = Exercise::create($data);

        // Devolvemos el ejercicio creado — 201 Created
        return response()->json($exercise, 201);
    }



    // Route Model Binding: Laravel resuelve el modelo por PK automáticamente
    // y retorna 404 si no existe

    public function show(Exercise $exercise)
    {
        // Retornamos el ejercicio encontrado — 200 OK
        return response()->json($exercise, 200);
    }


    public function update(UpdateExerciseRequest $request, Exercise $exercise)
    {

        // Verifica que el ejercicio pertenece al profesor autenticado
        $this->authorize('update', $exercise);

        // Actualizar solo los campos validados por UpdateExerciseRequest.
        $exercise->update($request->validated());

        // Retornar el modelo actualizado.
        return response()->json($exercise, 200);
    }


    public function destroy(Exercise $exercise)
    {

        // Verifica que el ejercicio pertenece al profesor autenticado
        $this->authorize('delete', $exercise);


        $exercise->delete();

        // Retornar 204 No Content para indicar que se ha eliminado correctamente.
        return response()->noContent();
    }
}
