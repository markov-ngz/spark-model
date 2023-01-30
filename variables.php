<?php

$users = [
    [
        'full_name' => 'Mickaël Andrieu',
        'mail' => 'user@exemple.com',
        'age' => 34,
        'password' => 'devine',
    ],
    [
        'full_name' => 'Mathieu Nebra',
        'mail' => 'mathieu.nebra@exemple.com',
        'age' => 34,
        'password' => 'MiamMiam',
    ],
    [
        'full_name' => 'Laurène Castor',
        'mail' => 'laurene.castor@exemple.com',
        'age' => 28,
        'password' => 'laCasto28',
    ],
];
$montagnes= [
    ['Nom' =>'everest',
    'altitude'=>8880,
    'Massif'=>'Himalaya',
'is_enabled'=>true],
['Nom'=>'Ararat',
'altitude'=>5137,
'Massif'=>'Anatolie',
'is_enabled'=>true],
['Nom'=>'Aconcagua',
'altitude'=>6700,
'Massif'=>'Cordilleres des Andes',
'is_enabled'=>true],
];
// variable pour limiter résultat affiché
if(isset($_GET['limit']) && is_numeric($_GET['limit'])) {
    $limit = (int) $_GET['limit'];
} else {
    $limit = 100;
}
?>