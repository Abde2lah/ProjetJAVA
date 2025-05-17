package org.mazeApp.model.algorithms;

import java.util.ArrayList;
import java.util.Stack;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;
import org.mazeApp.view.MazeView;

public class OnlyLeftSolver {

    private ArrayList<ArrayList<Edges>> graphMaze;
    private Graph graph;
    private MazeView mazeView;
    private int start;
    private int goal;
    private int vertexCount;
    private int columns;
    private int rows;

    /**
     * Crée un solveur qui tente de résoudre le labyrinthe en privilégiant 
     * les déplacements vers la gauche relative au sens de déplacement.
     * 
     * @param graph Le graphe représentant le labyrinthe
     * @param mazeView La vue du labyrinthe à mettre à jour
     */
    public OnlyLeftSolver(Graph graph, MazeView mazeView) {
        this.graph = graph;
        this.graphMaze = graph.getGraphMaze();
        this.vertexCount = graph.getVertexNb();
        this.columns = graph.getColumns();
        this.rows = graph.getRows();
        this.mazeView = mazeView;
        
    }

    /**
     * Directions cardinales possibles
     */
    private enum Direction {
        RIGHT(0),    // Est
        DOWN(1),     // Sud
        LEFT(2),     // Ouest
        UP(3);       // Nord
        
        private final int value;
        
        Direction(int value) {
            this.value = value;
        }
        
        /**
         * Calcule la direction après rotation à droite
         */
        public Direction turnRight() {
            return values()[(value + 1) % 4];
        }
        
        /**
         * Calcule la direction après rotation à gauche
         */
        public Direction turnLeft() {
            return values()[(value + 3) % 4]; // équivalent à (value - 1 + 4) % 4
        }
        
        /**
         * Calcule la direction après demi-tour
         */
        public Direction turnAround() {
            return values()[(value + 2) % 4];
        }
    }

    /**
     * Détermine si un déplacement vers un sommet voisin correspond à une direction donnée
     * 
     * @param current Le sommet actuel
     * @param neighbor Le sommet voisin potentiel
     * @param direction La direction souhaitée
     * @return true si le déplacement correspond à la direction
     */
    private boolean isDirection(int current, int neighbor, Direction direction) {
        int currentRow = current / columns;
        int currentCol = current % columns;
        int neighborRow = neighbor / columns;
        int neighborCol = neighbor % columns;
            
        switch (direction) {
            case RIGHT:
                return (neighborCol == currentCol + 1) && (neighborRow == currentRow);
            case DOWN:
                return (neighborRow == currentRow + 1) && (neighborCol == currentCol);
            case LEFT:
                return (neighborCol == currentCol - 1) && (neighborRow == currentRow);
            case UP:
                return (neighborRow == currentRow - 1) && (neighborCol == currentCol);
            default:
                return false;
        }
    }

    /**
     * Détermine la direction du mouvement entre deux sommets
     * 
     * @param from Sommet de départ
     * @param to Sommet d'arrivée
     * @return La direction du mouvement ou null si les sommets ne sont pas adjacents
     */
    private Direction getMovementDirection(int from, int to) {
        int fromRow = from / columns;
        int fromCol = from % columns;
        int toRow = to / columns;
        int toCol = to % columns;
        
        if (fromRow == toRow) {
            if (toCol == fromCol + 1) return Direction.RIGHT;
            if (toCol == fromCol - 1) return Direction.LEFT;
        } else if (fromCol == toCol) {
            if (toRow == fromRow + 1) return Direction.DOWN;
            if (toRow == fromRow - 1) return Direction.UP;
        }
        
        return null; // Les sommets ne sont pas adjacents
    }

    /**
     * Tente de se déplacer dans une direction spécifique à partir d'un sommet
     * 
     * @param current Sommet actuel
     * @param visited Tableau des sommets déjà visités
     * @param direction Direction préférentielle
     * @return Le sommet voisin dans la direction souhaitée ou -1 si impossible
     */
    private int tryMove(int current, boolean[] visited, Direction direction) {
        if (current < 0 || current >= graphMaze.size()) {
            return -1;
        }
        
        for (Edges edge : graphMaze.get(current)) {
            int neighbor = edge.getDestination();
            
            if (!visited[neighbor] && isDirection(current, neighbor, direction)) {
                return neighbor;
            }
        }
        
        return -1;  // Aucun mouvement possible dans cette direction
    }

    /**
     * Résout le labyrinthe en suivant le mur gauche (en tournant à gauche dès que possible)
     * 
     * @return Liste des étapes de la résolution
     */
    public ArrayList<ArrayList<Integer>> solveLeftSteps() {
        // Mise à jour des points de départ et d'arrivée
        this.start = mazeView.getStartIndex();
        this.goal = mazeView.getEndIndex();
        
        // Vérification des points de départ et d'arrivée
        if (start < 0 || goal < 0 || start >= vertexCount || goal >= vertexCount) {
            System.out.println("Points de départ ou d'arrivée invalides.");
            return new ArrayList<>();
        }
        
        boolean[] visited = new boolean[vertexCount];

        long startTime = System.currentTimeMillis();

        ArrayList<ArrayList<Integer>> allSteps = new ArrayList<>();

        long endTime = System.currentTimeMillis(); 
        long duration = endTime - startTime; 
        System.out.println("Durée de l'algorithme OnlyLeftSolver : " + duration + " ms");
        
        Stack<Integer> stack = new Stack<>();
        stack.push(start);
        visited[start] = true;
        
        ArrayList<Integer> path = new ArrayList<>();
        path.add(start);
        allSteps.add(new ArrayList<>(path));
        
        // Direction initiale (par défaut vers la droite sur la carte)
        Direction facing = Direction.RIGHT;
        
        while (!stack.isEmpty()) {
            int current = stack.peek();
            
            if (current == goal) {
                break;
            }
            
            // Algorithme de la main gauche:
            // 1. Essayer de tourner à gauche
            // 2. Si impossible, essayer tout droit
            // 3. Si impossible, essayer à droite 
            // 4. Si impossible, faire demi-tour
            
            Direction leftDirection = facing.turnLeft();
            Direction forwardDirection = facing;
            Direction rightDirection = facing.turnRight();
            Direction backDirection = facing.turnAround();
            
            // Essayer d'abord de tourner à gauche
            int next = tryMove(current, visited, leftDirection);
            
            if (next != -1) {
                // On peut tourner à gauche
                facing = leftDirection;
            } else {
                // Sinon, essayer d'aller tout droit
                next = tryMove(current, visited, forwardDirection);
                
                if (next != -1) {
                    // On peut aller tout droit
                    facing = forwardDirection;
                } else {
                    // Sinon, essayer de tourner à droite
                    next = tryMove(current, visited, rightDirection);
                    
                    if (next != -1) {
                        // On peut tourner à droite
                        facing = rightDirection;
                    } else {
                        // Sinon, faire demi-tour
                        next = tryMove(current, visited, backDirection);
                        
                        if (next != -1) {
                            // On fait demi-tour
                            facing = backDirection;
                        }
                    }
                }
            }
            
            if (next != -1) {
                // On a trouvé un chemin valide
                stack.push(next);
                visited[next] = true;
                path.add(next);
                allSteps.add(new ArrayList<>(path));
            } else {
                // Aucun mouvement possible, on doit reculer
                int removedVertex = stack.pop();
                
                // Mise à jour de la direction si on revient en arrière
                if (!stack.isEmpty() && !path.isEmpty()) {
                    int previous = stack.peek();
                    Direction backtrackDir = getMovementDirection(previous, removedVertex);
                    if (backtrackDir != null) {
                        facing = backtrackDir.turnAround();
                    }
                }
                
                if (!path.isEmpty()) {
                    path.remove(path.size() - 1);
                    // N'ajouter une étape que si le chemin a changé
                    if (!allSteps.isEmpty() && !path.equals(allSteps.get(allSteps.size() - 1))) {
                        allSteps.add(new ArrayList<>(path));
                    }
                }
            }
        }
        
        return allSteps;
    }

    /**
     * Visualise la résolution du labyrinthe
     */
    public void visualize() {
        // Vérifier que les points de départ et d'arrivée sont définis
        if (mazeView.getStartIndex() < 0 || mazeView.getEndIndex() < 0) {
            System.out.println("Veuillez définir un point de départ et un point d'arrivée.");
            return;
        }

        long startTime = System.currentTimeMillis();

        ArrayList<ArrayList<Integer>> steps = solveLeftSteps();
        
        if (steps.isEmpty()) {
            System.out.println("Impossible de résoudre le labyrinthe.");
            return;
        }
        long endTime = System.currentTimeMillis();   
        long duration = endTime - startTime;
        System.out.println("Durée de l'algorithme OnlyLeftSolver : " + duration + " ms");
        
        mazeView.visualiseStep(steps);
    }
}