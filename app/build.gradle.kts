plugins {
    // Plugin pour construire une application Java CLI
    application

    // Plugin JavaFX officiel (version stable)
    id("org.openjfx.javafxplugin") version "0.0.13"
}

repositories {
    // Utilise Maven Central pour les dépendances
    mavenCentral()
}

dependencies {
    // Utilise JUnit Jupiter pour les tests
    testImplementation(libs.junit.jupiter)

    // Runtime uniquement pour lancer les tests JUnit
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Dépendance utilisée dans l'application
    implementation(libs.guava)
}

// Configuration de la version de Java
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    // Classe principale de l'application
    mainClass = "org.mazeApp.Launcher"
}

// Configuration du plugin JavaFX
javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.media")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}


