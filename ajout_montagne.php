<?php session_start();?>

<DOCTYPE! html>
<html>
    <head>
        <meta charset="utf-8">
        <link rel="stylesheet" href="styles1.css">
    </head>
    <body>
        <h2>Ajout de montagne : </h2>
          <form action="<?php echo($rootUrl . 'post_ajout_montagne.php');?>" method="POST">
            <div class="mb-3">
                <label for="Nom">Nom</label>
                <input type="text" id="Nom" name="Nom" maxlenght=60>
            </div>
            <div class="mb-3">
                <label for="altitude">Altitude</label>
                <input type="number" id="altitude" name="altitude" maxlenght=11>
            </div>
            <div class="mb-3">
                <label for="massif">massif</label>
                <input type="text" id="massif" name="massif" maxlenght=60>
            </div>
            <div class="mb-3">
                <label for="premiere_ascension">premiere_ascension(AAAA-MM-JJ)</label>
                <input type="date" id="premiere_ascension" name="premiere_ascension">
            </div>
            <button type="submit">Envoyer</button>
        </form>

        <h2>Modif de montagne: </h2>
          <form action="<?php echo($rootUrl . 'modif_montagne.php');?>" method="POST">
          <div class="mb-3 visually hidden">
                <label for="id">Identifiant de la montagne</label>
                <input type="hidden" id="id" name="id" value="<?php echo($_GET['id']) ?>" >
            </div>
          <div class="mb-3">
                <label for="Nom">Nom</label>
                <input type="text" id="Nom" name="Nom" maxlenght=60>
            </div>
            <div class="mb-3">
                <label for="altitude">Altitude</label>
                <input type="number" id="altitude" name="altitude" maxlenght=11>
            </div>
            <div class="mb-3">
                <label for="massif">massif</label>
                <input type="text" id="massif" name="massif" maxlenght=60>
            </div>
            <div class="mb-3">
                <label for="premiere_ascension">premiere_ascension(AAAA-MM-JJ)</label>
                <input type="date" id="premiere_ascension" name="premiere_ascension">
            </div>
            <button type="submit">Envoyer</button>
        </form>
    </body>
</html>
