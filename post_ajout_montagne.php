<?php

include_once('ajout_montagne.php'); ?>
<?php //co à la bd
 try 
 {
    $db1 = new PDO('mysql:host=127.0.0.1;dbname=test;charset=UTF8','root','');
 }
catch (Exception $e)
{
    die('Erreur : '. $e->getMessage());
}
?>
<?php 
//verif formulaire
if (!isset($_POST['Nom'])
    || !isset($_POST['altitude'])
    || !isset($_POST['massif'])
    || !isset($_POST['premiere_ascension']) )
    {
        echo 'Il faut remplir tout les champs du formulaire !';
        return;
    
    }
// def des variables
    $Nom = $_POST['Nom'];
    $altitude = $_POST['altitude'];
    $massif = $_POST['massif'];
    $premiere_ascension = $_POST['premiere_ascension'];
    //insertion de base
$sqlquery3 = "INSERT INTO mount(Nom, altitude, massif, is_enabled, premiere_ascension)
 VALUES (:Nom,:altitude ,:massif , :is_enabled ,:premiere_ascension)";
$insertmounts = $db1 -> prepare($sqlquery3);
$insertmounts-> execute([
    'Nom' => $_POST['Nom'] ,
    'altitude' => $_POST['altitude'] ,
    'massif' => $_POST['massif'] ,
    'is_enabled' => 1 ,
    'premiere_ascension'=> $_POST['premiere_ascension'] ,
]);
?>