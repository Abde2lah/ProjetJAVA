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
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for the algorithms
 * This class is responsible for managing the algorithm buttons and their actions
 */
public class AlgorithmController{
    
    // Référence au contrôleur principal
    private MainControlleur mainController;
    
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
    private Label SpeedAnimationLabel;
    private Slider SpeedAnimationCursor;
    private int delay = 50;
    private VBox AlgoContainer;

    /**
     * Constructor which initializes the algorithm controller
     */
    public AlgorithmController(Graph graph, MainControlleur mainController) {
        this.mainController = mainController;
        initializeAlgorithmButtons();
        setupAlgorithmButtonActions();
    }
    
    /**
     * Initialize the algorithm buttons
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
        this.SpeedAnimationLabel = new Label("Speed : "+delay+" ms");
        this.SpeedAnimationCursor= new Slider(0, 100, 1);
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
        this.SpeedAnimationCursor.setPrefSize(100, 30);
        //styles for the animation label: 
        this.SpeedAnimationLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;");
        // Create a VBox to hold the buttons
        this.AlgoContainer = new VBox(10);
        this.AlgoContainer.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1; -fx-background-color: rgb(255, 254, 211);");
        
        // Add a title to the algorithm container
        javafx.scene.text.Text algoTitle = new javafx.scene.text.Text("Algorithmes");
        algoTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        this.AlgoContainer.getChildren().add(algoTitle);
        
        this.AlgoContainer.getChildren().addAll(
            this.DFSButton,
            this.BFSButton,
            this.AStarButton,
            this.DijkstraButton,
            this.PrimButton,
            this.KruskalButton,
            this.RightButton,
            this.LeftButton,
            this.RandomButton, 
            this.SpeedAnimationLabel,
            this.SpeedAnimationCursor
        );
    }
    
    /**
     * Set up the actions for the algorithm buttons
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
        
        // Other algorithm actions
        this.BFSButton.setOnAction(e -> {
            int colInputVal;
            int rowInputVal;
            MazeView mazeView = mainController.getMazeView();

            try {
                colInputVal = mainController.getColumnValue();
                rowInputVal = mainController.getRowValue();
            } catch(Exception err) {
                err.printStackTrace();
                return;
            }

            boolean isOperationAllowed = (colInputVal > 0 && colInputVal < 31);
            isOperationAllowed = (isOperationAllowed && (rowInputVal > 0 && rowInputVal < 31));

            if(isOperationAllowed) {
                ArrayList<Integer> path = executeBFSAlgorithm();
                mazeView.drawPath(path);  
            }   
        });
        
        this.AStarButton.setOnAction(e -> {
            GraphView graphView = mainController.getGraphView();
            MazeView mazeView = mainController.getMazeView();
            Graph model = mainController.getModel();
            AStarSolver aStarSolver = new AStarSolver(model, graphView, mazeView);
            aStarSolver.visualize();
        });

        this.DijkstraButton.setOnAction(e -> {
            Graph model = mainController.getModel();
            MazeView mazeView = mainController.getMazeView();
            GraphView graphView = mainController.getGraphView();

            DijkstraSolver dijkstraSolver = new DijkstraSolver(model, graphView, mazeView);
            dijkstraSolver.visualize();
        });

        this.PrimButton.setOnAction(e -> System.out.println("Prim non implémenté"));
        this.KruskalButton.setOnAction(e -> System.out.println("Kruskal non implémenté"));
        this.SpeedAnimationCursor.setOnMouseDragged(e -> {
            int delay = (int) SpeedAnimationCursor.getValue();
            SpeedAnimationLabel.setText("delay animation : " + delay+ " ms");
            mainController.getMazeView().setDelayResolverAnimation(delay);

        });
    }
    
    /**
     * Exécute l'algorithme DFS avec visualisation
     */
    private void executeDFSAlgorithm() {
        GraphView graphView = mainController.getGraphView();
        MazeView mazeView = mainController.getMazeView();
        Graph model = mainController.getModel();
        DFSsolver dfsSolver = new DFSsolver(model, graphView, mazeView);
        dfsSolver.visualize();
    }

    private void executeRandomAlgorithm() {
        Graph model = mainController.getModel();
        MazeView mazeView = mainController.getMazeView();
        RandomSolver randomSolver = new RandomSolver(model, mazeView);
        randomSolver.visualize();
    }
    private void executeOnlyRightlgorithm() {
        Graph model = mainController.getModel();
        MazeView mazeView = mainController.getMazeView();
        OnlyRightSolver onlyRightSolver = new OnlyRightSolver(model, mazeView);
        onlyRightSolver.visualize();
    }
    
    private void executeOnlyLeftlgorithm() {
        Graph model = mainController.getModel();
        MazeView mazeView = mainController.getMazeView();
        OnlyLeftSolver onlyLeftSolver = new OnlyLeftSolver(model, mazeView);
        onlyLeftSolver.visualize();
    }

    /**
     * Executes BFS Algorithm in the graph
     */
    private ArrayList<Integer> executeBFSAlgorithm() {
        Graph model = mainController.getModel();
        int verticesNb = model.getVertexNb();
        int startingPoint = mainController.getMazeView().getStartIndex();
        int endingPoint = mainController.getMazeView().getEndIndex();

        ArrayList<ArrayList<Edges>> graphAdjList = model.getGraphMaze();

        if (verticesNb < 0) {
            System.out.println("Graph has invalid number of Vertices");
            return new ArrayList<>();
        }

        if (startingPoint < 0 || endingPoint < 0) {
            System.out.println("Input valid coordinates for starting and ending points");
            return new ArrayList<>();
        }

        BFSsolver bfsSolver = new BFSsolver(verticesNb);
        return bfsSolver.visualize(startingPoint, endingPoint, graphAdjList);
    }


    
    /**
     * Get the algorithm buttons container
     */
    public VBox getAlgoContainer() {
        return this.AlgoContainer;
    }
}