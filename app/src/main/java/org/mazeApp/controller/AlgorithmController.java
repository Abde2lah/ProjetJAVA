package org.mazeApp.controller;

import java.util.ArrayList;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;
import org.mazeApp.model.algorithms.AStarSolver;
import org.mazeApp.model.algorithms.BFSsolver;
import org.mazeApp.model.algorithms.DFSsolver;
import org.mazeApp.model.algorithms.DijkstraSolver;
import org.mazeApp.model.algorithms.OnlyLeftSolver;
import org.mazeApp.model.algorithms.OnlyRightSolver;
import org.mazeApp.model.algorithms.RandomSolver;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.util.Duration;

/**
 * Contrôleur dédié aux algorithmes de
 *  parcours et génération de labyrinthe
 * Hérite de MazeController pour avoir accès aux modèles et vues
 */
public class AlgorithmController extends MainControlleur {
    
    // Boutons spécifiques aux algorithmes
    private Button DFSButton;
    private Button BFSButton;
    private Button AStarButton;
    private Button DijkstraButton;
    private Button PrimButton;
    private Button KruskalButton;
    private Button RightButton;
    private Button LeftButton;
    private Button RandomButton;
    
    /**
     * Constructeur qui appelle le constructeur parent
     */
    public AlgorithmController(Graph graph) {
        super(graph);
        initializeAlgorithmButtons();
        setupAlgorithmButtonActions();
    }
    
    /**
     * Initialise les boutons pour les algorithmes
     */
    private void initializeAlgorithmButtons() {
        // Create algorithm buttons
        this.DFSButton = new Button("DFS");
        this.BFSButton = new Button("BFS");
        this.AStarButton = new Button("A*");
        this.DijkstraButton = new Button("Dijkstra");
        this.PrimButton = new Button("Prim");
        this.KruskalButton = new Button("Kruskal");
        this.RightButton = new Button("Right");
        this.LeftButton = new Button("Left");
        this.RandomButton = new Button("Random");
        
        // Give the same size to algo buttons
        this.DFSButton.setPrefSize(100, 30);
        this.BFSButton.setPrefSize(100, 30);
        this.AStarButton.setPrefSize(100, 30);
        this.DijkstraButton.setPrefSize(100, 30);
        this.PrimButton.setPrefSize(100, 30);
        this.KruskalButton.setPrefSize(100, 30);
        this.RightButton.setPrefSize(100, 30);
        this.LeftButton.setPrefSize(100, 30);
        this.RandomButton.setPrefSize(100, 30);
        
        // Add buttons to the algo container from parent
        getAlgoButtonContainer().getChildren().addAll(
            this.DFSButton,
            this.BFSButton,
            this.AStarButton,
            this.DijkstraButton,
            this.PrimButton,
            this.KruskalButton,
            this.RightButton,
            this.LeftButton,
            this.RandomButton
        );
    }
    
    /**
     * Configure les actions des boutons d'algorithmes
     */
    private void setupAlgorithmButtonActions() {
        
        // Action to solve the maze with DFS
        this.DFSButton.setOnAction(e -> executeDFSAlgorithm());
        
        // Action to solve the maze with Random
        this.RandomButton.setOnAction(e -> executeRandomAlgorithm());
        
        // Action to solve the maze with OnlyRight
        this.RightButton.setOnAction(e -> executeOnlyRightlgorithm());
        
        // Action to solve the maze with OnlyLeft
        this.LeftButton.setOnAction(e -> executeOnlyLeftlgorithm());
        
        // Autres actions à implémenter
        this.BFSButton.setOnAction(e -> {
            

            int colInputVal;
            int rowInputVal;
            MazeView mazeView = getMazeView();

            try{
                colInputVal = getColumnValue();
                rowInputVal = getRowValue();

            }catch(Exception err){err.printStackTrace(); return;}

            
        
           boolean isOperationAllowed =  (colInputVal>0 && colInputVal<31) ? true : false; 
            isOperationAllowed = (isOperationAllowed &&(rowInputVal>0 && rowInputVal<31))? true : false;

            if(isOperationAllowed){
                ArrayList<Integer> path = executeBFSAlgorithm();
                mazeView.drawPath(path);  
            }   
        });
        this.AStarButton.setOnAction(e -> {
            GraphView graphView = getGraphView();
            MazeView mazeView = getMazeView();
            Graph model = getModel();
            AStarSolver aStarSolver = new AStarSolver(model, graphView, mazeView);
            aStarSolver.visualize();
        });

        this.DijkstraButton.setOnAction(e -> {
            Graph model = getModel();
            MazeView mazeView = getMazeView();
            GraphView graphView = getGraphView();

            DijkstraSolver dijkstraSolver = new DijkstraSolver(model, graphView, mazeView);
            dijkstraSolver.visualize();
        });

        this.PrimButton.setOnAction(e -> System.out.println("Prim non implémenté"));
        this.KruskalButton.setOnAction(e -> System.out.println("Kruskal non implémenté"));
    }
    
    /**
     * Exécute l'algorithme DFS avec visualisation
     */
    private void executeDFSAlgorithm() {
        GraphView graphView = getGraphView();
        MazeView mazeView = getMazeView();
        Graph model = getModel();
        DFSsolver dfsSolver = new DFSsolver(model, graphView, mazeView);
        dfsSolver.visualize();
    }

    private void executeRandomAlgorithm() {
        Graph model = getModel();
        MazeView mazeView = getMazeView();
        RandomSolver randomSolver = new RandomSolver(model, mazeView);
        randomSolver.visualize();
    }

    private void executeOnlyRightlgorithm() {
        Graph model = getModel();
        MazeView mazeView = getMazeView();
        OnlyRightSolver OnlyRightSolver = new OnlyRightSolver(model, mazeView);
        OnlyRightSolver.visualize();
    }
    
    private void executeOnlyLeftlgorithm() {
        Graph model = getModel();
        MazeView mazeView = getMazeView();
        OnlyLeftSolver OnlyLeftSolver = new OnlyLeftSolver(model, mazeView);
        OnlyLeftSolver.visualize();
    }

    /**
     * Executes BFS Algorithm in the graph
     */
    private ArrayList<Integer> executeBFSAlgorithm(){

        int verticesNb = getModel().getVertexNb();
        int startingPoint = getMazeView().getStartIndex() ;
        int endingPoint =  getMazeView().getEndIndex();

        ArrayList<ArrayList<Edges>> graphAdjList =  getModel().getGraphMaze();

        if( verticesNb < 0 ){
            System.out.println("Graph has invalid number of Vertices");
            
        }

        if (startingPoint < 0 || endingPoint < 0) {
            System.out.println("Input valid coordinates for starting and ending points");
            
        }

        BFSsolver bfsSolver = new BFSsolver(verticesNb);
        ArrayList<Integer >steps = bfsSolver.visualize(startingPoint,endingPoint,graphAdjList);

        return steps;
    }

    /**
     * Implémentation de l'animation de génération du labyrinthe
     */
    public void animateMazeGeneration() {
        try {
            int rows = getRowValue();
            int columns = getColumnValue();
            int seed = getSeedValue();

            System.out.println("Animation du labyrinthe " + rows + "x" + columns + " avec seed " + seed);

            // Créer un graphe vide
            Graph animatedGraph = Graph.emptyGraph(rows, columns);
            
            // Récupérer les étapes de génération en fonction de l'algorithme actuel
            ArrayList<Edges> steps = Graph.getCurrentGenerator().generate(rows, columns, seed);
            
            MazeView animatedMazeView = new MazeView(animatedGraph, getGraphView());

            setModel(animatedGraph);
            setMazeView(animatedMazeView);
            updateMazeViewInContainer(animatedMazeView);

            Timeline timeline = new Timeline();
            int delay = 50;

            for (int i = 0; i < steps.size(); i++) {
                final int index = i;
                KeyFrame frame = new KeyFrame(Duration.millis(i * delay), e -> {
                    Edges edge = steps.get(index);
                    animatedGraph.addEdge(edge.getSource(), edge.getDestination());
                    animatedMazeView.draw();
                });
                timeline.getKeyFrames().add(frame);
            }

            timeline.play();
        } catch (NumberFormatException e) {
            System.out.println("Erreur : entrez des valeurs valides pour lignes/colonnes/seed.");
        }
    }
}