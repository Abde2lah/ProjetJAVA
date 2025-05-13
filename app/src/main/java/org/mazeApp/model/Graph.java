package org.mazeApp.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Représentation d'un labyrinthe sous forme de graphe.
 * Utilise une liste d'adjacence pour modéliser les connexions entre les cellules.
 * Le graphe est acyclique et planaire (sans croisement d'arêtes).
 * 
 * @author Felipe Zani
 */
public class Graph {

    private int vertexCount;
    private int edgeCount;
    private int lignes;
    private int colonnes;
    private ArrayList<ArrayList<Edges>> graphMaze;
    
    /**
     * Crée un labyrinthe aléatoire avec l'algorithme de Kruskal modifié.
     * Le labyrinthe est représenté par un graphe planaire (sans croisement d'arêtes).
     * 
     * @param seed La graine pour la génération aléatoire
     * @param lignes Le nombre de lignes de la grille
     * @param colonnes Le nombre de colonnes de la grille
     */
    public Graph(int seed, int lignes, int colonnes) {
        int totalVertices = lignes * colonnes; // Nombre total de sommets dans la grille
        this.vertexCount = totalVertices;
        this.lignes = lignes;
        this.colonnes = colonnes;
        this.edgeCount = 0;
        this.graphMaze = new ArrayList<>();

        // Initialisation du graphe vide
        initializeGraph(totalVertices);
        
        // Génération du labyrinthe avec l'algorithme de Kruskal modifié pour grille
        generateGridMaze(seed, lignes, colonnes);
    }
    
    /**
     * Constructeur simplifié pour créer un labyrinthe carré.
     * 
     * @param seed La graine pour la génération aléatoire
     * @param size Le nombre de cellules par côté de la grille (taille totale = size*size)
     */
    public Graph(int seed, int size) {
        this(seed, size, size);
    }

    /**
     * Initialise un graphe vide.
     */
    private void initializeGraph(int totalVertices) {
        for (int i = 0; i < totalVertices; i++) {
            this.graphMaze.add(new ArrayList<>());
        }
    }
    
    /**
     * Génère un labyrinthe sur une grille rectangulaire.
     * Cette méthode assure que le graphe est planaire (sans croisement d'arêtes).
     */
    private void generateGridMaze(int seed, int lignes, int colonnes) {
        // Créer toutes les arêtes possibles dans une grille
        ArrayList<Edges> allEdges = createGridEdges(lignes, colonnes);
        
        // Mélanger les arêtes pour la génération aléatoire
        Collections.shuffle(allEdges, new Random(seed));
        
        // Appliquer l'algorithme de Kruskal pour créer un arbre couvrant
        applyKruskalAlgorithm(allEdges, lignes * colonnes);
        //Vous pouvez également ajouter une méthode pour générer un labyrinthe parfait
        // DFS ou Prim
    }
    
    /**
     * Crée les arêtes possibles dans une grille lignes x colonnes.
     * Chaque sommet est connecté uniquement à ses voisins orthogonaux (haut, bas, gauche, droite).
     */
    private ArrayList<Edges> createGridEdges(int lignes, int colonnes) {
        ArrayList<Edges> edges = new ArrayList<>();
        
        for (int row = 0; row < lignes; row++) {
            for (int col = 0; col < colonnes; col++) {
                int current = row * colonnes + col;
                
                // Connexion avec la cellule à droite (si elle existe)
                if (col < colonnes - 1) {
                    edges.add(new Edges(current, current + 1));
                }
                
                // Connexion avec la cellule en bas (si elle existe)
                if (row < lignes - 1) {
                    edges.add(new Edges(current, current + colonnes));
                }
            }
        }
        
        return edges;
    }
    
    /**
     * Applique l'algorithme de Kruskal pour créer un arbre couvrant minimal.
     */
    private void applyKruskalAlgorithm(ArrayList<Edges> edges, int totalVertices) {
        int[] parent = new int[totalVertices];
        
        // Initialiser chaque sommet comme son propre parent
        for (int i = 0; i < totalVertices; i++) {
            parent[i] = i;
        }
        
        // Parcourir toutes les arêtes triées (aléatoirement)
        for (Edges edge : edges) {
            int source = edge.getSource();
            int destination = edge.getDestination();
            
            // Vérifier si l'ajout de cette arête crée un cycle
            int sourceRoot = find(parent, source);
            int destRoot = find(parent, destination);
            
            if (sourceRoot != destRoot) {
                // Ajouter l'arête au graphe
                addEdgeBidirectional(source, destination);
                
                // Fusionner les deux composantes
                union(parent, sourceRoot, destRoot);
                
                // Arrêter quand on a un arbre couvrant (n-1 arêtes)
                if (this.edgeCount == totalVertices - 1) {
                    break;
                }
            }
        }
    }
    
    /**
     * Trouve la racine d'un sommet dans l'algorithme Union-Find.
     */
    private int find(int[] parent, int vertex) {
        if (parent[vertex] != vertex) {
            parent[vertex] = find(parent, parent[vertex]);
        }
        return parent[vertex];
    }
    
    /**
     * Fusionne deux composantes dans l'algorithme Union-Find.
     */
    private void union(int[] parent, int x, int y) {
        parent[x] = y;
    }
    
    /**
     * Ajoute une arête bidirectionnelle entre deux sommets.
     */
    private void addEdgeBidirectional(int source, int destination) {
        this.graphMaze.get(source).add(new Edges(source, destination));
        this.graphMaze.get(destination).add(new Edges(destination, source));
        this.edgeCount++;
    }
    
    /**
     * Ajoute une arête bidirectionnelle entre deux sommets.
     */
    public void addEdge(int source, int destination) {
        graphMaze.get(source).add(new Edges(source, destination));
        graphMaze.get(destination).add(new Edges(destination, source));
        edgeCount++;
    }
    
    /**
     * Supprime une arête entre deux sommets.
     */
    public void deleteEdge(int source, int destination) {
        graphMaze.get(source).removeIf(edge -> edge.getDestination() == destination);
        graphMaze.get(destination).removeIf(edge -> edge.getDestination() == source);
        edgeCount--;
    }
    
    /**
     * Ajoute un nouveau sommet au graphe.
     */
    public void addVertex() {
        graphMaze.add(new ArrayList<Edges>());
        vertexCount++;
    }
    
    /**
     * Supprime un sommet du graphe et toutes ses connexions.
     */
    public void removeVertex(int vertex) {
        // Compter le nombre d'arêtes à supprimer
        int edgesToRemove = graphMaze.get(vertex).size();
        
        // Supprimer le sommet
        graphMaze.remove(vertex);
        vertexCount--;
        edgeCount -= edgesToRemove;
        
        // Mettre à jour les références dans les autres sommets
        for (ArrayList<Edges> edges : graphMaze) {
            // Supprimer les connexions vers ce sommet
            edges.removeIf(edge -> edge.getDestination() == vertex);
            
            // Mettre à jour les indices des sommets supérieurs à celui supprimé
            for (Edges edge : edges) {
                if (edge.getDestination() > vertex) {
                    // Cette implémentation n'est pas idéale car Edges est immuable
                    // Il faudrait modifier Edges pour permettre de changer les valeurs
                    int newDest = edge.getDestination() - 1;
                    edges.remove(edge);
                    edges.add(new Edges(edge.getSource(), newDest));
                }
            }
        }
    }
    
    /**
     * Vide le graphe en supprimant toutes les arêtes.
     */
    public void clearGraph() {
        for (ArrayList<Edges> edges : graphMaze) {
            edges.clear();
        }
        edgeCount = 0;
    }
    
    /**
     * Retourne le nombre d'arêtes du graphe.
     */
    public int getEdgesNb() {
        return this.edgeCount;
    }

    /**
     * Retourne le nombre de sommets du graphe.
     */
    public int getVertexNb() {
        return this.vertexCount;
    }
    
    /**
     * Retourne le nombre de lignes du labyrinthe.
     */
    public int getLignes() {
        return this.lignes;
    }
    
    /**
     * Retourne le nombre de colonnes du labyrinthe.
     */
    public int getColonnes() {
        return this.colonnes;
    }
    
    /**
     * Retourne la structure du graphe sous forme de liste d'adjacence.
     */
    public ArrayList<ArrayList<Edges>> getGraphMaze() {
        return this.graphMaze;
    }
    
    /**
     * Représentation textuelle du graphe.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append(String.format("Labyrinthe %dx%d\n", lignes, colonnes));
        sb.append(String.format("Nombre de sommets: %d\n", vertexCount));
        sb.append(String.format("Nombre d'arêtes: %d\n\n", edgeCount));
        
        sb.append("Structure du graphe :\n");
        sb.append("------------------\n");
        
        for (int i = 0; i < graphMaze.size(); i++) {
            int row = i / colonnes;
            int col = i % colonnes;
            sb.append(String.format("Sommet %2d (%d,%d) : ", i, row, col));
            
            if (graphMaze.get(i).isEmpty()) {
                sb.append("(aucune connexion)");
            } else {
                for (int j = 0; j < graphMaze.get(i).size(); j++) {
                    Edges edge = graphMaze.get(i).get(j);
                    int destRow = edge.getDestination() / colonnes;
                    int destCol = edge.getDestination() % colonnes;
                    sb.append(String.format("(%d,%d)", destRow, destCol));
                    if (j < graphMaze.get(i).size() - 1) {
                        sb.append(", ");
                    }
                }
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }
}