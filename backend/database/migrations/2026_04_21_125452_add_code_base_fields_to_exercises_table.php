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
        Schema::table('exercises', function (Blueprint $table) {
            $table->json('parameters')->nullable()->after('function_name'); // [{"name":"a","type":"int"},...]
            $table->string('return_type')->nullable()->after('parameters'); // int|float|string|bool|array
            $table->json('templates')->nullable()->after('return_type');    // {python:..., javascript:..., java:...}
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('exercises', function (Blueprint $table) {
            $table->dropColumn(['parameters', 'return_type', 'templates']);
        });
    }
};
