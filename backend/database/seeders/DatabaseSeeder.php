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
        if (User::where('email', 'teacher@example.com')->exists()) {
            $this->command->info('Database already seeded, skipping.');
            return;
        }

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
        $this->call(LeaderboardSeeder::class);
        $this->call(TournamentSeeder::class);
        $this->call(SubmissionSeeder::class);
    }
}
