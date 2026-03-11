<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('exercises', function (Blueprint $table) {
            $table->id();
            $table->foreignId('user_id')->constrained()->cascadeOnDelete();
            $table->string('title');
            $table->text('description');
            $table->string('difficulty')->default('easy');
            $table->string('category');
            $table->text('solution_code')->nullable();
            $table->string('solution_language')->nullable();
            $table->boolean('is_verified')->default(false);
            $table->boolean('is_published')->default(false);
            $table->float('time_limit')->default(2);
            $table->integer('memory_limit')->default(128);
            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('exercises');
    }
};
