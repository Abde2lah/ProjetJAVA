package org.mazeApp.view.EditingView;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;

public class GraphEditor {

    /**
     * Adds edge between two vertices in the graph. 
     * @param graph Graph to edit 
     * @param source Index of source vertex
     * @param destination Index of destination vertex 
     * @return Returns true if the edges was already added else otherwise (error or already existing)
     */
    public boolean addEdge(Graph graph, int source, int destination) {
        if (graph == null) return false;
        if (source == destination) return false;

        int vertexCount = graph.getVertexNb();
        if (source < 0 || destination < 0 || source >= vertexCount || destination >= vertexCount) {
            System.err.println("Error: Vertex indices invalid: " + source + " -> " + destination);
            return false;
        }

        // Verification if edges already exist
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

        //Adding vertices in a not oriented graph, edges are added in mirror ex: 2->1, 1->2 
        graph.getGraphMaze().get(source).add(new Edges(source, destination));
        graph.getGraphMaze().get(destination).add(new Edges(destination, source));

        System.out.println("Edge successfully added between " + source + " and " + destination);
        return true;
    }

    /**
     * Removing edges between vertices in the graph. 
     * @param graph Le graphe à modifier
     * @param source Indice du sommet source
     * @param destination Indice du sommet destination
     * @return Returns true if edge was successfully removed, false otherwise 
     */
    public boolean removeEdge(Graph graph, int source, int destination) {
        if (graph == null) return false;
        if (source == destination) return false;

        boolean removed = false;

        //Removes the selected edge from the sourceEdges list
        for (Edges edge : new java.util.ArrayList<>(graph.getGraphMaze().get(source))) {
            if (edge.getDestination() == destination) {
                graph.getGraphMaze().get(source).remove(edge);
                removed = true;
                break;
            }
        }
        //Removes the selected edge from the destinationEdges list
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
