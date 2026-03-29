<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('submission_results', function (Blueprint $table) {
            $table->id();
            $table->foreignId('submission_id')->constrained()->cascadeOnDelete();
            $table->foreignId('test_case_id')->constrained()->cascadeOnDelete();
            $table->boolean('passed')->default(false);
            $table->text('output')->nullable();
            $table->text('error')->nullable();
            $table->float('execution_time')->nullable();
            $table->integer('memory')->nullable();
            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('submission_results');
    }
};
