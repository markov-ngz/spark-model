<?php session_start(); ?>
<!DOCTYPE html>
<html lang="fr">
 <head>
    <meta charset="utf-8" >
    <title> Page à acces limité </title>
</head>
<body>
    <?php include('header.php'); ?>
    <!-- inclusion formulaire de connexion -->
    <?php include_once('login.php'); ?>
    <h1> Infos à afficher </h1>
    <!-- si utilisateur existe, afficher recette -->
   <?php if (isset($_SESSION['LOGGED_USER'])) : ?>
    <?php foreach(get_montagne($montagne,$limit) as $montagne):?>
     <article>
        <h3><?php echo $montagne['Nom']; ?></h3>
        <?php echo $montagne['altitude']; ?> 
        <?php echo $montagne['Massif']; ?>
    </article>
    <?php endforeach ?>
    <?php endif; ?>
    <?php include('footer.php'); ?>
</body>
</html>