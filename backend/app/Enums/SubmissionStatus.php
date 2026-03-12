<?php

namespace App\Enums;

enum SubmissionStatus : string
{
    case Pending     = 'pending';
    case Running     = 'running';
    case Accepted    = 'accepted';
    case WrongAnswer = 'wrong_answer';
    case Error       = 'error';
    case Timeout     = 'timeout';
}
