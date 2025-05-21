package org.mazeApp.view.EditingView;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;

public class GraphEditor {

    /**
     * Ajoute une arête entre source et destination dans le graphe.
     * 
     * @param graph Le graphe à modifier
     * @param source Indice du sommet source
     * @param destination Indice du sommet destination
     * @return true si l'arête a été ajoutée, false sinon (déjà existante ou erreur)
     */
    public boolean addEdge(Graph graph, int source, int destination) {
        if (graph == null) return false;
        if (source == destination) return false;

        int vertexCount = graph.getVertexNb();
        if (source < 0 || destination < 0 || source >= vertexCount || destination >= vertexCount) {
            System.err.println("Error: Vertex indices invalid: " + source + " -> " + destination);
            return false;
        }

        // Vérifie si l'arête existe déjà
        boolean edgeExists = false;
        for (Edges edge : graph.getGraphMaze().get(source)) {
            if (edge.getDestination() == destination) {
                edgeExists = true;
                break;
            }
        }

        if (edgeExists) {
            System.out.println("Edge already exists between " + source + " and " + destination);
            return false;
        }

        // Ajoute l'arête dans les deux sens (graphe non orienté)
        graph.getGraphMaze().get(source).add(new Edges(source, destination));
        graph.getGraphMaze().get(destination).add(new Edges(destination, source));

        System.out.println("Edge successfully added between " + source + " and " + destination);
        return true;
    }

    /**
     * Supprime une arête entre source et destination dans le graphe.
     * 
     * @param graph Le graphe à modifier
     * @param source Indice du sommet source
     * @param destination Indice du sommet destination
     * @return true si l'arête a été supprimée, false sinon (introuvable ou erreur)
     */
    public boolean removeEdge(Graph graph, int source, int destination) {
        if (graph == null) return false;
        if (source == destination) return false;

        boolean removed = false;

        // Supprime dans la liste du source
        for (Edges edge : new java.util.ArrayList<>(graph.getGraphMaze().get(source))) {
            if (edge.getDestination() == destination) {
                graph.getGraphMaze().get(source).remove(edge);
                removed = true;
                break;
            }
        }

        // Supprime dans la liste du destination
        for (Edges edge : new java.util.ArrayList<>(graph.getGraphMaze().get(destination))) {
            if (edge.getDestination() == source) {
                graph.getGraphMaze().get(destination).remove(edge);
                removed = true;
                break;
            }
        }

        if (removed) {
            System.out.println("Edge successfully removed between " + source + " and " + destination);
            return true;
        } else {
            System.out.println("Edge not found between " + source + " and " + destination);
            return false;
        }
    }
}
