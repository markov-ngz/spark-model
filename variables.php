
<?php 
 try 
 {
    $db = new PDO('mysql:host=localhost;dbname=relief_du_monde;charset=UTF8','root','tripleAAA9');
 }
catch (Exception $e)
{
    die('Erreur : '. $e->getMessage());
}
?>
<?php
$usersquery = 'SELECT * FROM users';
$usersstatement = $db->prepare($usersquery);
$usersstatement->execute();
$users = $usersstatement->fetchAll();
foreach($users as $user) {
?>
<p><?php echo $user['mail']; ?></p>    
<?php
}
?>
<?php
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