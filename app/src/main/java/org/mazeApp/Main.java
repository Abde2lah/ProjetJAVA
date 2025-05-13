package org.mazeApp;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;

public class Main extends Application {

    private static final int CELL_SIZE = 20;
    private static int ROWS = 2;
    private static int COLS = 10;

    private boolean[][][] walls;
    private boolean[][] visited;
    private Canvas canvas;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Home screen
        Button createMazeBtn = new Button("Créer un labyrinthe");
        Button displayMazeBtn = new Button("Voir les anciens labyrinthes");

        VBox homeRoot = new VBox(20);
        homeRoot.setAlignment(Pos.CENTER);
        homeRoot.getChildren().addAll(createMazeBtn, displayMazeBtn);
        Scene startingScene = new Scene(homeRoot, 1920, 1080);

        // Maze algorithm choice screen
        Button DFSCreation = new Button("Labyrinthe avec DFS");
        Button BFSCreation = new Button("Labyrinthe avec BFS");
        Button AStarCreation = new Button("Labyrinthe avec A*");
        Button DijkstraCreation = new Button("Labyrinthe avec Dijkstra");
        Button PrimCreation = new Button("Labyrinthe avec Prim");

        VBox creationPane = new VBox(20);
        creationPane.setAlignment(Pos.CENTER);
        creationPane.getChildren().addAll(
            DFSCreation, BFSCreation, AStarCreation, DijkstraCreation, PrimCreation
        );
        Scene creationScene = new Scene(creationPane, 1920, 1080);

        // Scene where maze will be drawn
        StackPane canvasPane = new StackPane();
        Scene mazeScene = new Scene(canvasPane, 1920, 1080);

        // Size input screen
        Text rowLabel = new Text("Nombre de lignes :");
        TextField rowInput = new TextField();

        Text colLabel = new Text("Nombre de colonnes :");
        TextField colInput = new TextField();

        Button validateSizeBtn = new Button("Valider les dimensions");

        VBox sizePane = new VBox(20, rowLabel, rowInput, colLabel, colInput, validateSizeBtn);
        sizePane.setAlignment(Pos.CENTER);
        Scene sizeScene = new Scene(sizePane, 1920, 1080);

        // Button to go to size selection
        createMazeBtn.setOnAction(event -> {
            primaryStage.setScene(sizeScene);
        });

        // Validate size input and go to algorithm choice only if all is good
        validateSizeBtn.setOnAction(event -> {
            try {
                ROWS = Integer.parseInt(rowInput.getText());
                COLS = Integer.parseInt(colInput.getText());
                if (ROWS <= 0 || COLS <= 0) throw new NumberFormatException();

                // Re-init arrays based on new size
                walls = new boolean[ROWS][COLS][4];
                visited = new boolean[ROWS][COLS];

                primaryStage.setScene(creationScene);
            } catch (NumberFormatException e) {
                rowInput.clear();
                colInput.clear();
            }
        });

        // Maze generation with DFS
        DFSCreation.setOnAction(event -> {
            initializeMaze();
            generateMazeDFS();

            canvas = new Canvas(COLS * CELL_SIZE, ROWS * CELL_SIZE);
            drawMaze(canvas.getGraphicsContext2D());

            canvasPane.getChildren().setAll(canvas);
            primaryStage.setScene(mazeScene);
        });

        // Initial screen
        primaryStage.setTitle("CYNAPSE");
        primaryStage.setScene(startingScene);
        primaryStage.show();
    }

    private void initializeMaze() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Arrays.fill(walls[r][c], true);
                visited[r][c] = false;
            }
        }
    }


    private void generateMazeDFS() {
        int seed = 232322;
        Random rndGenerator = new Random(seed);
        
        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{0, 0});
        visited[0][0] = true;

        while (!stack.isEmpty()) {
            int[] current = stack.peek();
            int r = current[0];
            int c = current[1];
            /*Adding pseudo random variable create the possibility of obtaining same maze with a same seed
             * * each seed generate an unique maze
             * */
            
            List<Integer> directions = new ArrayList<>(List.of(0,1,2,3));

            Collections.shuffle(directions, rndGenerator);
            boolean moved = false;

            for (int dir : directions) {
                int nr = r, nc = c;
                switch (dir) {
                    case 0: nr--; break;
                    case 1: nc++; break;
                    case 2: nr++; break;
                    case 3: nc--; break;
                }

                if (nr >= 0 && nc >= 0 && nr < ROWS && nc < COLS && !visited[nr][nc]) {
                    visited[nr][nc] = true;
                    walls[r][c][dir] = false;
                    walls[nr][nc][(dir + 2) % 4] = false;
                    stack.push(new int[]{nr, nc});
                    moved = true;
                    break;
                }
            }

            if (!moved) stack.pop();
        }
    }
    private void drawMaze(GraphicsContext gc) {
        gc.clearRect(0, 0, COLS * CELL_SIZE, ROWS * CELL_SIZE);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                double x = c * CELL_SIZE;
                double y = r * CELL_SIZE;

                if (walls[r][c][0]) gc.strokeLine(x, y, x + CELL_SIZE, y);  // Nord
                if (walls[r][c][1]) gc.strokeLine(x + CELL_SIZE, y, x + CELL_SIZE, y + CELL_SIZE); // Est
                if (walls[r][c][2]) gc.strokeLine(x, y + CELL_SIZE, x + CELL_SIZE, y + CELL_SIZE); // Sud
                if (walls[r][c][3]) gc.strokeLine(x, y, x, y + CELL_SIZE);  // Ouest
            }
        }
    }
}
