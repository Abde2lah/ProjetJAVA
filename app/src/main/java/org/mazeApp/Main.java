package org.mazeApp;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.*;

public class Main extends Application {

    private static final int CELL_SIZE = 40;
    private static final int ROWS = 10;
    private static final int COLS = 10;

    private boolean[][][] walls = new boolean[ROWS][COLS][4]; // N, E, S, W
    private boolean[][] visited = new boolean[ROWS][COLS];
    private Canvas canvas;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Home screen buttons
        Button createMazeBtn = new Button("Créer un labyrinthe");
        Button displayMazeBtn = new Button("Voir les anciens labyrinthes");

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(createMazeBtn, displayMazeBtn);

        Scene startingScene = new Scene(root, 400, 300);
        primaryStage.setTitle("CYNAPSE");
        primaryStage.setScene(startingScene);
        primaryStage.show();

        // MAze creation screen
        canvas = new Canvas(COLS * CELL_SIZE, ROWS * CELL_SIZE);
        StackPane canvasPane = new StackPane(canvas);
        Scene mazeScene = new Scene(canvasPane, COLS * CELL_SIZE, ROWS * CELL_SIZE);

        //Resolution screen
        Button DFSResolution = new Button("Labyrinthe crée avec un algorithme DFS");

        VBox resolutionPane = new VBox(20);
        resolutionPane.setAlignment(Pos.CENTER);
        resolutionPane.getChildren().addAll(DFSResolution);

        Scene resolutionScene = new Scene(resolutionPane, COLS * CELL_SIZE, ROWS * CELL_SIZE);
        DFSResolution.setOnAction(event -> {
            primaryStage.setScene(resolutionScene);
        });

        // Button action Create Maze button
        createMazeBtn.setOnAction(event -> {
            primaryStage.setScene(resolutionScene);
        });

        // Resolution with DFS
        DFSResolution.setOnAction(event -> {
            initializeMaze();
            generateMazeDFS();
            drawMaze(canvas.getGraphicsContext2D());
            primaryStage.setScene(mazeScene);
        });
    }

    // Initialise toutes les cellules avec des murs
    private void initializeMaze() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Arrays.fill(walls[r][c], true);  // 4 murs
                visited[r][c] = false;
            }
        }
    }

    // Génère un labyrinthe parfait (DFS)
     // Génère un labyrinthe parfait (DFS)
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
                    case 0: nr--; break; // N
                    case 1: nc++; break; // E
                    case 2: nr++; break; // S
                    case 3: nc--; break; // W
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

            if (!moved) {
                stack.pop();
            }
        }
    }
    //
    private void drawMaze(GraphicsContext gc) {
        gc.clearRect(0, 0, COLS * CELL_SIZE, ROWS * CELL_SIZE);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                double x = c * CELL_SIZE;
                double y = r * CELL_SIZE;

                if (walls[r][c][0]) gc.strokeLine(x, y, x + CELL_SIZE, y);               // Nord
                if (walls[r][c][1]) gc.strokeLine(x + CELL_SIZE, y, x + CELL_SIZE, y + CELL_SIZE); // Est
                if (walls[r][c][2]) gc.strokeLine(x, y + CELL_SIZE, x + CELL_SIZE, y + CELL_SIZE); // Sud
                if (walls[r][c][3]) gc.strokeLine(x, y, x, y + CELL_SIZE);               // Ouest
            }
        }
    }
}
