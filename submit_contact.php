
<?php 
if ((!isset($_GET['email']) || !filter_var($_GET['email'], FILTER_VALIDATE_EMAIL))
|| (!isset($_GET['Nom']) || empty($_GET['Nom']))
|| (!isset($_GET['requetes']) || empty($_GET['requetes']))
)
{
    echo('Il faut un email, un nom et un message valide pour soumettre le formulaire.');
    return;
}
//test si fichier est bien envoyé et pas d'erreur
if (isset($FILES['photo_ascension']) && $_FILES['photo_ascension']['error'] ==0)
{//verif taille fichier
    if($_FILES['photo_ascension']['size'] <=10000000)
    {// recupere extension fichier
        $fileinfo = pathinfo($_FILES['photo_ascension']['name']);
        $extension = $fileinfo['extension'];
        $allowedExtensions = ['jpg','png','jpeg','gif'];
        // verifie si extension valide
        if (in_array($extension,$allowedExtensions))
        {//validation et stockage du fichier
        move_uploaded_file($_FILES['photo_ascension']['tmp_name'], 'uploads/'.
        basename($_FILES['photo_ascension']['name']));
        echo "L'envoi du fichier a bien été effectué";
        }
    }
}
// to do : gérer espaces, doublons, scripts
?>
<DOCTYPE! html>
<html lang="fr">
    <head>
        <meta charset="utf-8" >
        <link href="styles1.css" rel="stylesheet">
</head>
<body>
    <h2> Votre demande de contact à bien été prise en compte<h2><br>
    <p> Nous vous répondrons dans les plus brefs délais, merci.</p>
    <div>
    <h3>Rappel de vos informations : <h3>
        <p>Votre Nom : <?php echo htmlspecialchars($_GET['Nom']); ?> </p>
        <p> Votre email : <?php echo htmlspecialchars($_GET['email']); ?></p>
</div>
</body>
</html>