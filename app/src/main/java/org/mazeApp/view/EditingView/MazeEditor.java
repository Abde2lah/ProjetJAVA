package org.mazeApp.view.EditingView;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;

public class MazeEditor {
    
    /**
     * Ajoute une connexion (mur supprimé) entre deux cellules.
     * 
     * @param graph Le graphe représentant le labyrinthe
     * @param cell1 Première cellule (indice)
     * @param cell2 Deuxième cellule (indice)
     * @return true si ajout effectué, false sinon
     */
    public boolean addConnection(Graph graph, int cell1, int cell2) {
        if (graph == null) return false;
        if (cell1 == cell2) return false;
        int vertexCount = graph.getVertexNb();
        if (cell1 < 0 || cell2 < 0 || cell1 >= vertexCount || cell2 >= vertexCount) {
            System.err.println("Indices de cellule invalides : " + cell1 + " - " + cell2);
            return false;
        }

        if (areConnected(graph, cell1, cell2)) {
            System.out.println("Les cellules sont déjà connectées : " + cell1 + " - " + cell2);
            return false;
        }

        graph.getGraphMaze().get(cell1).add(new Edges(cell1, cell2));
        graph.getGraphMaze().get(cell2).add(new Edges(cell2, cell1));
        System.out.println("Connexion ajoutée entre " + cell1 + " et " + cell2);
        return true;
    }

    /**
     * Supprime une connexion (mur ajouté) entre deux cellules.
     * 
     * @param graph Le graphe représentant le labyrinthe
     * @param cell1 Première cellule (indice)
     * @param cell2 Deuxième cellule (indice)
     * @return true si suppression effectuée, false sinon
     */
    public boolean removeConnection(Graph graph, int cell1, int cell2) {
        if (graph == null) return false;
        if (cell1 == cell2) return false;
        int vertexCount = graph.getVertexNb();
        if (cell1 < 0 || cell2 < 0 || cell1 >= vertexCount || cell2 >= vertexCount) {
            System.err.println("Indices de cellule invalides : " + cell1 + " - " + cell2);
            return false;
        }

        boolean removed = false;

        for (Edges edge : new java.util.ArrayList<>(graph.getGraphMaze().get(cell1))) {
            if (edge.getDestination() == cell2) {
                graph.getGraphMaze().get(cell1).remove(edge);
                removed = true;
                break;
            }
        }

        for (Edges edge : new java.util.ArrayList<>(graph.getGraphMaze().get(cell2))) {
            if (edge.getDestination() == cell1) {
                graph.getGraphMaze().get(cell2).remove(edge);
                removed = true;
                break;
            }
        }

        if (removed) {
            System.out.println("Connexion supprimée entre " + cell1 + " et " + cell2);
            return true;
        } else {
            System.out.println("Connexion non trouvée entre " + cell1 + " et " + cell2);
            return false;
        }
    }

    /**
     * Vérifie si deux cellules sont connectées.
     * 
     * @param graph Le graphe
     * @param cell1 Première cellule
     * @param cell2 Deuxième cellule
     * @return true si connectées, false sinon
     */
    public boolean areConnected(Graph graph, int cell1, int cell2) {
        if (graph == null) return false;
        if (cell1 == cell2) return true;
        int vertexCount = graph.getVertexNb();
        if (cell1 < 0 || cell2 < 0 || cell1 >= vertexCount || cell2 >= vertexCount) return false;

        for (Edges edge : graph.getGraphMaze().get(cell1)) {
            if (edge.getDestination() == cell2) {
                return true;
            }
        }
        return false;
    }

    /**
     * Modifie le graphe en basculant la présence d'un mur entre deux cellules.
     * Si connectées, supprime la connexion, sinon l'ajoute.
     * 
     * @param graph Le graphe représentant le labyrinthe
     * @param cell1 Première cellule
     * @param cell2 Deuxième cellule
     * @return true si modification effectuée, false sinon
     */
    public boolean toggleConnection(Graph graph, int cell1, int cell2) {
        if (areConnected(graph, cell1, cell2)) {
            return removeConnection(graph, cell1, cell2);
        } else {
            return addConnection(graph, cell1, cell2);
        }
    }
}
