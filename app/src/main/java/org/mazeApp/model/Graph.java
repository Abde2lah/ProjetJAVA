package org.mazeApp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.mazeApp.model.generator.KruskalGenerator;
import org.mazeApp.model.generator.MazeGenerator;

/**
 * Représentation d'un labyrinthe sous forme de graphe.
 * Utilise une liste d'adjacence pour modéliser les connexions entre cellules.
 * Le graphe est acyclique et planaire (aucune arête croisée).
 * 
 * Authors: Felipe Zani, Jeremy Perbost
 */
public class Graph {

    private int vertexCount;
    private int edgeCount;
    private int rows;
    private int columns;
    private ArrayList<ArrayList<Edges>> graphMaze;
    private ArrayList<Edges> generationSteps;
    private static MazeGenerator currentGenerator = new KruskalGenerator();

    /**
     * Retourne la liste des étapes de génération (pour l'animation).
     */
    public ArrayList<Edges> getGenerationSteps() {
        return generationSteps;
    }

    /**
     * Constructeur principal utilisant un algorithme de Kruskal modifié.
     * 
     * @param seed Graine de génération aléatoire
     * @param rows Nombre de lignes dans la grille
     * @param columns Nombre de colonnes dans la grille
     */
    public Graph(int seed, int rows, int columns) {
        int totalVertices = rows * columns;

        this.vertexCount = totalVertices;
        this.rows = rows;
        this.columns = columns;
        this.edgeCount = 0;
        this.graphMaze = new ArrayList<>();
        this.generationSteps = new ArrayList<>();

        initializeGraph(totalVertices);
        generateGridMaze(seed, rows, columns);
    }

    /**
     * Constructeur simplifié pour créer un labyrinthe carré.
     * 
     * @param seed Graine de génération aléatoire
     * @param size Taille d'un côté de la grille (taille totale = size * size)
     */
    public Graph(int seed, int size) {
        this(seed, size, size);
    }

    /**
     * Factory method pour créer un graphe vide (sans génération).
     */
    public static Graph emptyGraph(int rows, int columns) {
        return new Graph(true, rows, columns);
    }

    /**
     * Constructeur interne utilisé pour créer un graphe vide sans génération.
     */
    private Graph(boolean empty, int rows, int columns) {
        int totalVertices = rows * columns;
        this.vertexCount = totalVertices;
        this.rows = rows;
        this.columns = columns;
        this.edgeCount = 0;
        this.graphMaze = new ArrayList<>();
        this.generationSteps = new ArrayList<>();
        initializeGraph(totalVertices);
        
        // Ne génère pas le labyrinthe si empty est true
        if (!empty) {
            generateGridMaze(0, rows, columns);
        }
    }

    /**
     * Initialise un graphe vide (liste d'adjacence).
     */
    private void initializeGraph(int totalVertices) {
        for (int i = 0; i < totalVertices; i++) {
            this.graphMaze.add(new ArrayList<>());
        }
    }

    /**
     * Génère un labyrinthe planaire (sans croisements).
     */
    private void generateGridMaze(int seed, int rows, int columns) {
        generationSteps = currentGenerator.generate(rows, columns, seed);
        
        // Construire le graphe à partir des étapes
        for (Edges edge : generationSteps) {
            addEdgeBidirectional(edge.getSource(), edge.getDestination());
        }
    }
    // Ajoute une arête bidirectionnelle
    private void addEdgeBidirectional(int source, int destination) {
        this.graphMaze.get(source).add(new Edges(source, destination));
        this.graphMaze.get(destination).add(new Edges(destination, source));
        this.edgeCount++;
    }

    public void removeVertex(int vertex) {
        int edgesToRemove = graphMaze.get(vertex).size();
        graphMaze.remove(vertex);
        vertexCount--;
        edgeCount -= edgesToRemove;

        for (ArrayList<Edges> edges : graphMaze) {
            edges.removeIf(edge -> edge.getDestination() == vertex);

            for (Edges edge : new ArrayList<>(edges)) {
                if (edge.getDestination() > vertex) {
                    int newDest = edge.getDestination() - 1;
                    edges.remove(edge);
                    edges.add(new Edges(edge.getSource(), newDest));
                }
            }
        }
    }

    public void clearGraph() {
        for (ArrayList<Edges> edges : graphMaze) {
            edges.clear();
        }
        edgeCount = 0;
    }

    public int getEdgesNb() {
        return this.edgeCount;
    }

    public int getVertexNb() {
        return this.vertexCount;
    }

    public int getRows() {
        return this.rows;
    }

    public int getColumns() {
        return this.columns;
    }
    public void addEdge(int source, int destination) {
        this.graphMaze.get(source).add(new Edges(source, destination));
        this.graphMaze.get(destination).add(new Edges(destination, source));
        this.edgeCount++;
    }
    public ArrayList<ArrayList<Edges>> getGraphMaze() {
        return this.graphMaze;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("Labyrinthe %dx%d\n", rows, columns));
        sb.append(String.format("Nombre de sommets : %d\n", vertexCount));
        sb.append(String.format("Nombre d’arêtes : %d\n\n", edgeCount));
        sb.append("Structure du graphe :\n");
        sb.append("---------------------\n");

        for (int i = 0; i < graphMaze.size(); i++) {
            int row = i / columns;
            int col = i % columns;
            sb.append(String.format("Sommet %2d (%d,%d) : ", i, row, col));

            if (graphMaze.get(i).isEmpty()) {
                sb.append("(pas de connexion)");
            } else {
                for (Edges edge : graphMaze.get(i)) {
                    int destRow = edge.getDestination() / columns;
                    int destCol = edge.getDestination() % columns;
                    sb.append(String.format("→ %2d (%d,%d) ", edge.getDestination(), destRow, destCol));
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Définit l'algorithme de génération à utiliser
     */
    public static void setGenerator(MazeGenerator generator) {
        currentGenerator = generator;
    }

    /**
     * Retourne le générateur courant
     */
    public static MazeGenerator getCurrentGenerator() {
        return currentGenerator;
    }

}
