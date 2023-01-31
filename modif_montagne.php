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
if (!isset($_POST['id']) && !isset($_POST['Nom'])
    || !isset($_POST['altitude'])
    || !isset($_POST['massif'])
    || !isset($_POST['premiere_ascension']) )
    {
        echo 'Il faut remplir tout les champs du formulaire !';
        return;
    
    }

    $Nom = $_POST['Nom'];
    $altitude = $_POST['altitude'];
    $massif = $_POST['massif'];
    $premiere_ascension = $_POST['premiere_ascension'];
    //insertion de base
$updatequery = "UPDATE mount SET Nom = :Nom , altitude = :altitude , massif = :massif , is_enabled = :is_enabled , premiere_ascension = :premiere_ascension WHERE mount_id = :id";
$updatemounts = $db1 -> prepare($updatequery);
$updatemounts-> execute([
    'Nom' => $_POST['Nom'] ,
    'altitude' => $_POST['altitude'] ,
    'massif' => $_POST['massif'] ,
    'is_enabled' => 1 ,
    'premiere_ascension'=> $_POST['premiere_ascension'] ,
    'id' => $_POST['id']
]) or die(print_r($db1->errorInfo()));
?>