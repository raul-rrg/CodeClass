<?php

namespace Database\Seeders;

use App\Models\User;
use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;

class DatabaseSeeder extends Seeder
{
    use WithoutModelEvents;

    /**
     * Seed the application's database.
     */
    public function run(): void
    {
        // User::factory(10)->create();

        User::factory()->create([
            'name'  => 'Teacher',
            'email' => 'teacher@example.com',
            'role'  => 'teacher',
        ]);

        User::factory()->create([
            'name'  => 'Student',
            'email' => 'student@example.com',
            'role'  => 'student',
        ]);

        $this->call(ExerciseSeeder::class);
    }
}
