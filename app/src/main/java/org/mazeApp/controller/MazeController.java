package org.mazeApp.controller;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import org.mazeApp.model.Graph;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;

/**
 * Contrôleur principal pour l'application de labyrinthe
 * Gère les interactions entre le modèle (Graph) et les vues
 * Contient également les composants UI et leur logique
 */
public class MazeController {
    
    private Graph model;
    private GraphView graphView;
    private MazeView mazeView;
    
    // Composants UI
    private TextField rowInput;
    private TextField colInput;
    private TextField seedInput;
    private Button clearButton;
    private Button generateButton;
    private VBox inputContainer;
    private VBox graphContainer;
    private VBox mazeContainer;
    
    public MazeController(Graph model, GraphView graphView, MazeView mazeView) {
        this.model = model;
        this.graphView = graphView;
        this.mazeView = mazeView;
        
        // Initialisation des composants UI
        initializeUIComponents();
        setupButtonActions();
        setupContainers();
    }
    
    /**
     * Initialise tous les composants de l'interface utilisateur
     */
    private void initializeUIComponents() {
        // Création des champs de saisie
        Text rowLabel = new Text("Nombre de lignes :");
        this.rowInput = new TextField("5");  // Valeur par défaut
        Text colLabel = new Text("Nombre de colonnes :");
        this.colInput = new TextField("5");  // Valeur par défaut
        Text seedLabel = new Text("Graine :");
        this.seedInput = new TextField("42");  // Valeur par défaut
        
        // Création des boutons
        this.clearButton = new Button("Effacer");
        this.generateButton = new Button("Générer");
        
        // Création des conteneurs
        this.inputContainer = new VBox(10);
        this.graphContainer = new VBox(10);
        this.mazeContainer = new VBox(10);
        
        HBox buttonContainer = new HBox(10);
        buttonContainer.getChildren().addAll(this.clearButton, this.generateButton);
        
        // Ajouter les champs de saisie et les boutons au conteneur d'entrée
        inputContainer.getChildren().addAll(
            rowLabel, this.rowInput, 
            colLabel, this.colInput, 
            seedLabel, this.seedInput,
            buttonContainer
        );
    }
    
    /**
     * Configure les actions pour les boutons
     */
    private void setupButtonActions() {
        this.generateButton.setOnAction(e -> {
            try {
                int lignes = Integer.parseInt(this.rowInput.getText());
                int colonnes = Integer.parseInt(this.colInput.getText());
                int seed;
                
                if (this.seedInput.getText().isEmpty()) {
                    seed = (int) (Math.random() * Integer.MAX_VALUE);
                    this.seedInput.setText(String.valueOf(seed));
                } else {
                    seed = Integer.parseInt(this.seedInput.getText());
                }
                
                generateMaze(lignes, colonnes, seed);
            } catch (NumberFormatException ex) {
                System.out.println("Erreur: Veuillez entrer des nombres valides");
            }
        });

        this.clearButton.setOnAction(e -> {
            clearMaze();
        });
    }
    
    /**
     * Configure les conteneurs pour les vues
     */
    private void setupContainers() {
        // Créer des étiquettes pour chaque vue
        Label graphLabel = new Label("Vue du Graphe");
        Label mazeLabel = new Label("Vue du Labyrinthe");
        
        // Styliser les étiquettes
        graphLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        mazeLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        
        // Ajouter les vues à leurs conteneurs
        this.graphContainer.getChildren().addAll(graphLabel, this.graphView);
        this.mazeContainer.getChildren().addAll(mazeLabel, this.mazeView);
    }
    
    /**
     * Génère un nouveau labyrinthe avec les paramètres spécifiés
     */
    public void generateMaze(int rows, int columns, int seed) {
        if (rows < 2 || columns < 2) {
            System.out.println("Erreur: Les dimensions doivent être d'au moins 2x2");
            return;
        }
        
        System.out.println("Génération d'un labyrinthe " + rows + "x" + columns + " avec la graine " + seed);
        
        // Créer le nouveau graphe avec les paramètres fournis
        this.model = new Graph(seed, rows, columns);
        refreshViews();
    }
    
    /**
     * Efface le graphe actuel
     */
    public void clearMaze() {
        System.out.println("Effacement du graphe");
        this.model.clearGraph();
        refreshViews();
    }
    
    /**
     * Rafraîchit les vues pour refléter l'état actuel du modèle
     */
    public void refreshViews() {
        this.graphView.draw(this.model);
        this.mazeView.draw(this.model);
    }
    
    // Getters pour le modèle et les conteneurs UI
    
    /**
     * Retourne le modèle actuel
     */
    public Graph getModel() {
        return model;
    }
    
    /**
     * Définit un nouveau modèle et met à jour les vues
     */
    public void setModel(Graph model) {
        this.model = model;
        refreshViews();
    }
    
    /**
     * Retourne le conteneur d'entrée (champs et boutons)
     */
    public VBox getInputContainer() {
        return this.inputContainer;
    }
    
    /**
     * Retourne le conteneur de la vue graphe
     */
    public VBox getGraphContainer() {
        return this.graphContainer;
    }
    
    /**
     * Retourne le conteneur de la vue labyrinthe
     */
    public VBox getMazeContainer() {
        return this.mazeContainer;
    }
}