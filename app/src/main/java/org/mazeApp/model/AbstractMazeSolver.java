package org.mazeApp.model;

import java.util.ArrayList;
import java.util.List;
import org.mazeApp.view.GraphView;
import org.mazeApp.view.MazeView;

/**
 * Classe abstraite implémentant les fonctionnalités communes aux solveurs
 */
public abstract class AbstractMazeSolver implements MazeSolver {
    protected Graph model;
    protected GraphView graphView;
    protected MazeView mazeView;
    protected List<Integer> finalPath;
    protected long executionTime;
    
    public AbstractMazeSolver() {
        this.finalPath = new ArrayList<>();
        this.executionTime = 0;
    }
    
    @Override
    public MazeSolver setup(Graph graph, GraphView graphView, MazeView mazeView) {
        this.model = graph;
        this.graphView = graphView;
        this.mazeView = mazeView;
        return this;
    }
    
    @Override
    public long getExecutionTime() {
        return executionTime;
    }
    
    @Override
    public List<Integer> getFinalPath() {
        return finalPath;
    }
    
    /**
     * Méthode utilitaire pour mesurer le temps d'exécution d'une opération
     * @param operation L'opération à exécuter
     */
    protected void measureExecutionTime(Runnable operation) {
        long startTime = System.currentTimeMillis();
        operation.run();
        long endTime = System.currentTimeMillis();
        this.executionTime = endTime - startTime;
    }
}
