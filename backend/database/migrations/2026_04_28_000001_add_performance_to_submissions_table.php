<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::table('submissions', function (Blueprint $table) {
            $table->float('max_execution_time')->nullable()->after('compile_output');
            $table->integer('max_memory')->nullable()->after('max_execution_time');
        });
    }

    public function down(): void
    {
        Schema::table('submissions', function (Blueprint $table) {
            $table->dropColumn(['max_execution_time', 'max_memory']);
        });
    }
};
