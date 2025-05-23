package org.mazeApp.view.EditingView;

import org.mazeApp.model.Edges;
import org.mazeApp.model.Graph;

public class MazeEditor {
    
    /**
     * Adds connexion between two cells in the graph which deletes a wall.
     * @param graph The graph representation of the maze
     * @param cell1 Cell index of the maze 
     * @param cell2 Cell index of the maze
     * @return Returns true if a connection between cells was successfully added, false otherwise
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
     * Delete a connection between two cells, therefore a wall is created between them. 
     * @param graph The graph representation of the maze
     * @param cell1 Cell index of the maze 
     * @param cell2 Cell index of the maze
     * @return Returns true if a connection between cells was successfully removed, false otherwise
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
     * Verify if there is a connection between two cells.
     * @param graph The graph representation of the maze
     * @param cell1 Cell index of the maze 
     * @param cell2 Cell index of the maze
     * @return Returns true if a connection between cells exists, false otherwise
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
     *Modify the graph by toggling the presence of a wall between two cells
     * @param graph The graph representation of the maze
     * @param cell1 Cell index of the maze 
     * @param cell2 Cell index of the maze
     * @return Returns true if a connexion between cells was toggle, false otherwise
     */

    public boolean toggleConnection(Graph graph, int cell1, int cell2) {
        if (areConnected(graph, cell1, cell2)) {
            return removeConnection(graph, cell1, cell2);
        } else {
            return addConnection(graph, cell1, cell2);
        }
    }
}
