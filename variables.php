
<?php 
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
$sqlquery = 'SELECT * FROM mount ORDER BY altitude';
$mountstatement = $db->prepare($sqlquery);
$mountstatement->execute();
$mounts = $mountstatement->fetchAll();
foreach($mounts as $mount) {
?>
<p><?php echo $mount['Nom']; ?></p>    
<?php
}
?>
<!-- ajout unitaire de donnée dans la db -->
<?php /* passage en commentaire
//ajout d'une montagne dans la bd
$sqlquery2 = 'INSERT INTO mount(Nom, altitude,massif, is_enabled, premiere_ascension)
                VALUES (:Nom, :altitude, :massif, :is_enabled, :premiere_ascension)';
         $insertmount = $db-> prepare($sqlquery2) ;      
                $insertmount->execute([
                    'Nom' => 'Cervin',
                    'altitude' => 4478,
                    'massif'=>'Alpes',
                    'is_enabled' => 1,
                    'premiere_ascension'=>'1865-07-14',//AAAA-MM-JJ
                ]); */
?>

<?php
// variable pour limiter résultat affiché
if(isset($_GET['limit']) && is_numeric($_GET['limit'])) {
    $limit = (int) $_GET['limit'];
} else {
    $limit = 100;
}
?>