<?php

namespace App\Jobs;

use App\Models\Exercise;
use Illuminate\Bus\Queueable;
use Illuminate\Contracts\Queue\ShouldQueue;
use Illuminate\Foundation\Bus\Dispatchable;
use Illuminate\Queue\InteractsWithQueue;
use Illuminate\Queue\SerializesModels;
use Illuminate\Support\Facades\Http;
use Illuminate\Support\Facades\Log;

/**
 * Job que traduce automáticamente los campos de texto de un ejercicio al idioma contrario.
 *
 * Se despacha al crear/actualizar un ejercicio. Llama a la API de DeepL con todos
 * los campos en un único request y guarda las traducciones en las columnas JSON
 * de spatie/laravel-translatable del modelo Exercise.
 */
class TranslateExerciseJob implements ShouldQueue
{
    use Dispatchable, InteractsWithQueue, Queueable, SerializesModels;

    // Mapeo locale interno → código DeepL (DeepL exige "EN-US", no "en")
    private const DEEPL_LOCALES = ['es' => 'ES', 'en' => 'EN-US'];
    private const FIELDS        = ['title', 'description', 'short_description'];

    public function __construct(
        protected Exercise $exercise,
        protected string $sourceLocale
    ) {}

    public function handle(): void
    {
        // Solo hay 2 idiomas: el destino es siempre el contrario al origen
        $targetLocale = $this->sourceLocale === 'es' ? 'en' : 'es';

        // Extraemos los textos a traducir en el orden definido por self::FIELDS. Si no hay traducción previa, enviamos string vacío (DeepL no acepta null).
        $texts = array_map(
            fn($field) => $this->exercise->getTranslation($field, $this->sourceLocale, false) ?? '',
            self::FIELDS
        );

        // Llamada a API de DeepL para traducir los campos. Devuelve un array de traducciones en el mismo orden que $texts.
        $response = Http::withHeaders([
            'Authorization' => 'DeepL-Auth-Key ' . config('services.deepl.key'),
        ])->post('https://api-free.deepl.com/v2/translate', [
            'text'        => $texts,
            'target_lang' => self::DEEPL_LOCALES[$targetLocale],
        ]);

        if ($response->failed()) {
            Log::error('DeepL translation failed', [
                'exercise_id' => $this->exercise->id,
                'status'      => $response->status(),
            ]);
            return;
        }


        $translations = $response->json('translations');

        // Guardamos cada traducción en su campo correspondiente del modelo Exercise, usando el método setTranslation
        foreach (self::FIELDS as $i => $field) {
            $translated = $translations[$i]['text'] ?? null;
            if ($translated) {
                $this->exercise->setTranslation($field, $targetLocale, $translated);
            }
        }

        $this->exercise->save();
    }
}
