<?php

namespace App\Enums;

enum SubmissionStatus : string
{
    case Pending     = 'pending';
    case Running     = 'running';
    case Accepted    = 'accepted';   // todos los test cases pasaron
    case Rejected    = 'rejected';   // al menos un test case falló (status global de la submission)
    case WrongAnswer = 'wrong_answer'; // no usado en Submission; reservado para compatibilidad
    case Error       = 'error';      // el job falló tras agotar los reintentos
    case Timeout     = 'timeout';
}
