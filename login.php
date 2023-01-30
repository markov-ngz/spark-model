<?php
//retenir email personne connectée pour 1 an
setcookie('LOGGED_USER', 'utilisateur@exemple.com',
[
    'expires' => time() + 365*24*3600,
    'secure'=> true,
    'httponly'=> true,
])
?>
Bonjour <?php echo $_COOKIE['LOGGER_USER']; ?>
<?php 
//soumission formulaire
if (isset($_POST['mail']) && isset($_POST['Password'])) {
    foreach ($users as $user) {
        if ($user['mail'] === $_POST['mail'] &&
        $user['Password'] === $_POST['Password']) 
        {
            //enregistrement user en session
            $_SESSION['LOGGED_USER'] = $user['mail'];
        }
    }
}
?>
<?php
//Validation formulaire
if (isset($_POST['mail']) && isset($_POST['Password'])){
    foreach ($users as $user) {
    if ($user['mail'] === $_POST['mail'] &&
    $user['Password'] === $_POST['Password']) {
      $_SESSION['LOGGED_USER']= $user['mail']
      ;
    } else {
        $errorMessage = sprintf('Les informations envoyées ne permettent pas de vous identifier : (%s/%s)',
        $_POST['mail'],
        $_POST['Password']);
    }
    }
    }

?>
<?php
// si user non identifié, afficher formulaire de connexion
if (!isset($_SESSION['LOGGED_USER'])):
?>
 <form method="post" action="logged.php">
 <?php
// si error msg, afficher signalement
if(isset($errorMessage)): ?>
 <div class="alert aler_danger" role="alert" >
   <?php  echo $errorMessage ?>
</div>
<?php endif; ?>
    <div class="mb-3">
<legend> Vos identifiants :</legend>
<label for="mail">mail:</label>
<input type="email" name="Mail" id="Mail" size="30" maxlenght="60"
placeholder="Ex: kikoulol01@coco.com" ariadescribedby="email-help">
        <div id="email-help" class="form-text"> Email utilisé lors de la création du compte
        </div>
    </div>
    <div class="mb-3">
<label for="Password">Mot de Passe:</label>
<input type="password" name="Password" id="Password" size="30" maxlenght="100" 
placeholder="a&!1aaaa" >
    </div>
    <button type="submit" class="btn btn-primary"> Envoyer </button>
</form>
<!-- si utilisateur identifié, echo msg succes -->
<?php else: ?>
    <div class="aler alert-succes" role="alert">
        Bonjour <?php echo $_SESSION['LOGGED_USER']; ?> et bienvenue sur la site !
</div>
<?php endif; ?>


   





