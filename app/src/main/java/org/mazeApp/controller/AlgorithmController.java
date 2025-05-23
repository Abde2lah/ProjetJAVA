/**
 * Controller responsible for managing maze solving algorithms.
 * <p>
 * This class initializes and manages the buttons associated with different
 * maze-solving algorithms, handles their execution and animation,
 * and updates the user interface with execution time and path length.
 * </p>
 * 
* @author Abdellah, Felipe, Jeremy, Shawrov, Melina
 * @version 1.0
 */

package org.mazeApp.controller;

import java.util.ArrayList;
import java.util.List;

import org.mazeApp.model.Graph;
import org.mazeApp.model.MazeSolver;
import org.mazeApp.model.algorithms.AStarSolver;
import org.mazeApp.model.algorithms.BFSsolver;
import org.mazeApp.model.algorithms.DFSsolver;
import org.mazeApp.model.algorithms.DijkstraSolver;
import org.mazeApp.model.algorithms.OnlyLeftSolver;
import org.mazeApp.model.algorithms.OnlyRightSolver;
import org.mazeApp.model.algorithms.RandomSolver;
import org.mazeApp.model.algorithms.UserPlaySolver;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for the algorithms
 * This class is responsible for managing the algorithm buttons and their actions
 */
public class AlgorithmController {
    
    // Référence to the main controller
    private MainControlleur mainController;
    
    // Buttons for the algos
    private Button DFSButton;
    private Button BFSButton;
    private Button AStarButton;
    private Button DijkstraButton;
    private Button UserPlayButton;
    private Button RightButton;
    private Button LeftButton;
    private Button RandomButton;
    private Button StopButton;         
    private Label SpeedAnimationLabel;
    private Label TimeExecutionLabel;   
    private Label PathLengthLabel;     
    private Label totalVisitedSquares;
    private Slider SpeedAnimationCursor;
    private int delay = 50;
    private VBox AlgoContainer;
    private Timeline animationTimeline;
    private CheckBox animationCBK;
    /**
     * Constructs an AlgorithmController with access to the graph and the main controller.
     *
     * @param graph the graph representing the maze structure
     * @param mainController the main application controller
     */
    public AlgorithmController(Graph graph, MainControlleur mainController){


        this.mainController = mainController;
        initializeAlgorithmButtons();
        setupAlgorithmButtonActions();
        setupAllButtons();
    }
    
    /**
     * Initializes the algorithm buttons and their interactivity
     */
    private void initializeAlgorithmButtons() {
        // Create algorithm buttons
        this.DFSButton = new Button("DFS");
        this.BFSButton = new Button("BFS");
        this.AStarButton = new Button("A*");
        this.DijkstraButton = new Button("Dijkstra");
        this.UserPlayButton = new Button("User");
        this.RightButton = new Button("Right");
        this.LeftButton = new Button("Left");
        this.RandomButton = new Button("Random");
        this.StopButton = new Button("Arrêter");
        this.animationCBK = new CheckBox("Animation");
        this.animationCBK.setSelected(true);
        this.TimeExecutionLabel = new Label("Temps : 0 ms");
        this.PathLengthLabel = new Label("Longueur : 0 cases");
        this.totalVisitedSquares = new Label("Nombre de cases traitées: 0 cases");
        this.SpeedAnimationLabel = new Label("Speed : "+delay+" ms");
        this.SpeedAnimationCursor= new Slider(1, 100, 1);
         
        // Give the same size to algo buttons
        this.DFSButton.setPrefSize(100, 30);
        this.BFSButton.setPrefSize(100, 30);
        this.AStarButton.setPrefSize(100, 30);
        this.DijkstraButton.setPrefSize(100, 30);
        this.UserPlayButton.setPrefSize(100, 30);
        this.RightButton.setPrefSize(100, 30);
        this.LeftButton.setPrefSize(100, 30);
        this.RandomButton.setPrefSize(100, 30);
        this.StopButton.setPrefSize(100, 30);
        this.SpeedAnimationCursor.setPrefSize(100, 30);
        
        //styles for the animation label: 
        this.SpeedAnimationLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;");
        this.TimeExecutionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;");
        this.PathLengthLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;");
        this.totalVisitedSquares.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;");
        //Style for the checkbox
        this.animationCBK.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;"); 

        // Styles for the stop buttons (in red)
        this.StopButton.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white;");
        //Sets initial visibility
        this.SpeedAnimationLabel.setVisible(false);
        this.SpeedAnimationCursor.setVisible(false);  
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
            this.UserPlayButton,
            this.RightButton,
            this.LeftButton,
            this.RandomButton,
            this.StopButton,          
            this.TimeExecutionLabel,  
            this.PathLengthLabel,  
            this.totalVisitedSquares,
            this.animationCBK,
            this.SpeedAnimationLabel,
            this.SpeedAnimationCursor
        );
    }
    
    /**
     * Set up the actions for the algorithm buttons.
     */
    private void setupAlgorithmButtonActions() {
        // Button to stop the current animation
        this.StopButton.setOnAction(e -> {
            clearPreviousAnimation();
            System.out.println("Animation arrêtée");
        });
        
        //let the users solve by himself the maze
        this.UserPlayButton.setOnAction(e -> {
            clearPreviousAnimation();  
            System.out.println("Player mode activated : use ZQSD to solve the maze");
            long startTime = System.currentTimeMillis();
            MazeView mazeView = mainController.getMazeView();
            Graph graph = mainController.getCurrentGraph();

            if (mazeView.getStartIndex() < 0 || mazeView.getEndIndex() < 0) {
                System.out.println("Please define Start and end point");
                return;
            }

            UserPlaySolver userSolver = new UserPlaySolver(mazeView, graph);
           //Indicating when to execute the task to draw the paths on the screen 
            userSolver.setOnCompletion(() -> {
              ArrayList<Integer> finalPath = userSolver.getFinalPath();
              ArrayList<Integer> intermediatePath = userSolver.getPathVisitedSquares();
              ArrayList<ArrayList<Integer>> pathsToDraw = new ArrayList<ArrayList<Integer>>();

              pathsToDraw.add(intermediatePath);
              pathsToDraw.add(finalPath);
              
              mazeView.nonAnimationVisualizeStep(pathsToDraw);

              updatePathLengthLabel(finalPath);
              updateVisitedSquaresLabel(intermediatePath.size()>=0 ? intermediatePath.size() : 0);
              updateTimeExecutionLabel(System.currentTimeMillis() - startTime);
              
            });
            userSolver.attachToScene();
        });
       //toggle animation speed modifiers visibility 
        this.animationCBK.setOnAction(e->{
            this.SpeedAnimationCursor.setVisible(animationCBK.isSelected());
            this.SpeedAnimationLabel.setVisible(animationCBK.isSelected()); 
        });


        this.SpeedAnimationCursor.setOnMouseDragged(e -> {
            int delay = (int) SpeedAnimationCursor.getValue();
            SpeedAnimationLabel.setText("Speed : " + delay + " ms");
            mainController.getMazeView().setDelayResolverAnimation(delay);
        });
    }
    
    /**
     * Starts a recurring timer to check if the animation has completed,
     * and updates the path length label accordingly.
     */
    private void setupAnimationListener() {
        // Stop the current timeline if it exists
        if (this.animationTimeline != null) {
            this.animationTimeline.stop();
        }
        
        this.animationTimeline = new Timeline(new KeyFrame(Duration.millis(500), event -> {
            MazeView mazeView = mainController.getMazeView();
            

            if (mazeView != null && !mazeView.isAnimationRunning()) {
                // Try to find the path and his height
                Graph model = mainController.getModel();
                int start = mazeView.getStartIndex();
                int end = mazeView.getEndIndex();
                
                // Use DFS to easier search the path
                DFSsolver dfsSolver = new DFSsolver(model, null, null);
                List<Integer> path = dfsSolver.findPath(start, end);
                updatePathLengthLabel(path);
                
                // Stop the timer
                this.animationTimeline.stop();
            }
        }));
        this.animationTimeline.setCycleCount(Timeline.INDEFINITE);
        this.animationTimeline.play();
    }
    
    /**
     * Update the execution time
     * @param durationMs Duration in millisecondes
     */
    private void updateTimeExecutionLabel(long durationMs) {
        TimeExecutionLabel.setText("Temps : " + durationMs + " ms");
    }
    /**
     * Update the path length
     * @param path Path found which contains the nodes
     */
    private void updatePathLengthLabel(List<Integer> path) {
        int length = (path != null) ? path.size() : 0;
        PathLengthLabel.setText("Longueur : " + length + " cases");
    }

    /**
     * Updates total visited Vertices counter.
     * @param nbVisitedPaths Represents the total number of visited paths 
     * @param pathLength Represents the length of the path
     * */
    private void updateVisitedSquaresLabel(int nbVisitedSquares){
      totalVisitedSquares.setText("Total de cases traitées: "+nbVisitedSquares);
    }
    
    /**
     * Creates and initializes the solver instance based on the selected algorithm type.
     *
     * @param solverType the type of algorithm to be executed (e.g., "DFS", "AStar")
     * @return the configured MazeSolver instance
     * @throws IllegalArgumentException if the solverType is unknown
     */
    private MazeSolver createSolver(String solverType) {
        Graph model = mainController.getModel();
        GraphView graphView = mainController.getGraphView();
        MazeView mazeView = mainController.getMazeView();
        
        MazeSolver solver;
        
        switch (solverType) {
            case "DFS":
                solver = new DFSsolver();
                break;
            case "BFS":
                solver = new BFSsolver(model.getVertexNb());
                break;
            case "AStar":
                solver = new AStarSolver();
                break;
            case "Dijkstra":
                solver = new DijkstraSolver();
                break;
            case "Random":
                solver = new RandomSolver();
                break;
            case "Right":
                solver = new OnlyRightSolver();
                break;
            case "Left":
                solver = new OnlyLeftSolver();
                break;
            default:
                throw new IllegalArgumentException("Unknonw solver type : " + solverType);
        }
        
        return solver.setup(model, graphView, mazeView);
    }


    /**
     * Configures an individual algorithm button with its execution logic.
     *
     * @param button the button to set up
     * @param solverType the type of solver to associate with this button
     */
    private void setupAlgorithmButton(Button button, String solverType) {
        button.setOnAction(e -> {
            try {
                clearPreviousAnimation();
                
                MazeView mazeView = mainController.getMazeView();
                
                // Validate that both start and end points are set before proceeding
                if (mazeView.getStartIndex() < 0 || mazeView.getEndIndex() < 0) {
                    System.out.println("Please define both start and end points before running the algorithm.");
                    return; // Exit early if points aren't set
                }
                
                MazeSolver solver = createSolver(solverType);
                
                // Computes the elapsed time  
                long startTime = System.currentTimeMillis();
                
                List<Integer> path = solver.findPath(mazeView.getStartIndex(), mazeView.getEndIndex());
                
                updatePathLengthLabel(path);
                updateVisitedSquaresLabel(solver.getvisitedVerticesNumber() >= 0? solver.getvisitedVerticesNumber() : 0);
                
                if (animationCBK.isSelected()) {
                    solver.visualize();
                    setupAnimationListener();
                } else {
                    solver.nonAnimationVisualize();
                }
                
                // Update execution time
                long endTime = System.currentTimeMillis();
                updateTimeExecutionLabel(endTime - startTime);
                
            } catch (Exception ex) {
                System.err.println("Error executing " + solverType + ": " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    /**
     * Initializes all available algorithm buttons with their respective solvers.
     */
    private void setupAllButtons() {
        setupAlgorithmButton(DFSButton, "DFS");
        setupAlgorithmButton(BFSButton, "BFS");
        setupAlgorithmButton(AStarButton, "AStar");
        setupAlgorithmButton(DijkstraButton, "Dijkstra");
        setupAlgorithmButton(RandomButton, "Random");
        setupAlgorithmButton(RightButton, "Right");
        setupAlgorithmButton(LeftButton, "Left");
    }
    
    /**
     * Get the algorithm buttons container
     */
    public VBox getAlgoContainer() {
        return this.AlgoContainer;
    }
    
    /**
     * Clear the display from previous animations
     */
    private void clearPreviousAnimation() {
        MazeView mazeView = mainController.getMazeView();
        if (mazeView != null) {
            // Stop every current animation
            if (this.animationTimeline != null) {
                this.animationTimeline.stop();
            }
            
            // Stop the animation
            mazeView.stopAnimation();
            
            // Reset the display
            mazeView.clearAnimations();
            
            // Update forced
            mazeView.draw();
            
            // Reset labels
            updateTimeExecutionLabel(0);
            updatePathLengthLabel(null);
        }
    }
}
