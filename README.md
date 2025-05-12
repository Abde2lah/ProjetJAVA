# ☕LABYRINTHE JAVA☕
## ⚠️ Sommaire ⚠️
1. [📓 TUTORIEL D'INSTALLATION 📓](#tutoriel-dinstallation-)
2. [📖WIKI📖](#wiki)
3. [🧑‍🤝‍🧑PARTICIPANTS🧑‍🤝‍🧑](#participants)


## 📓TUTORIEL D'INSTALLATION 📓
1) Télécharger [gradle](https://gradle.org/install/)
2) modifiez les variable d'environnement et mettez le nom du chemin qui contient le dossier bin : C:\gradle\gradle-7.x\bin
3) vérifiez l'installation ``gradle -v``
4) Créez un dossier vide. Ca sera le dossier du projet
5) modifiez le fichier et mettez ``#org.gradle.configuration-cache=true`` en commentaire
Modifications de build.gradle.kts:
rajoutez la ligne de code suivante n'importe ou dans le fichier : 
``
javafx{
    modules("javafx.controls", "javafx.fxml")
}
``
remplacez la ligne de code suivante : 
``
plugins {
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}
``
6) Ouvrez un terminal a l'emplacement du dossier. et taper la commande ``gradle build``
7) tapez ``gradle run``
8) Enjoy l'installation 😄!
## 📖WIKI📖
## 🧑‍🤝‍🧑PARTICIPANTS🧑‍🤝‍🧑
Jérémy 

Fellipe

Abdella

Sharov

Mélanie
