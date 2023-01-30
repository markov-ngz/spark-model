<?php
//fonction qui va chercher la montagne
function get_montagne(array $montagne, int $limit) : array
{
    $valid_montagne = [];
    $counter = 0;

    foreach($montagnes as $montagne) {
        if ($counter == $limit) {
            return $valid_montagne;
        }

        if ($montagne['is_enabled']) {
            $valid_montagne[] = $montagne;
            $counter++;
        }
    }

    return $valid_montagne;
}