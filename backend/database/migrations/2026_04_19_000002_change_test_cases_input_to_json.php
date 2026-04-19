<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        // input es un array JSON de argumentos: [3, 5], [6], etc.
        // El wrapper lo lee desde stdin y lo desempaqueta como argumentos de la función del alumno
        Schema::table('test_cases', function (Blueprint $table) {
            $table->json('input')->nullable()->change();
        });
    }

    public function down(): void
    {
        Schema::table('test_cases', function (Blueprint $table) {
            $table->text('input')->nullable()->change();
        });
    }
};
