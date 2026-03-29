<?php

namespace App\Enums;

enum ProgrammingLanguage : string
{
    case Python     = 'python';
    case Java       = 'java';
    case JavaScript = 'javascript';

    public function judge0Id(): int
    {
        return match($this) {
            self::Python     => 71,
            self::Java       => 62,
            self::JavaScript => 63,
        };
    }
}
