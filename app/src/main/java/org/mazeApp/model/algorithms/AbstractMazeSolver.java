package org.mazeApp.model.algorithms;

import java.util.ArrayList;
import java.util.List;
import org.mazeApp.model.Graph;
import org.mazeApp.model.MazeSolver;
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
    protected int visitedVerticesNb;
    
    public AbstractMazeSolver() {
        this.finalPath = new ArrayList<>();
        this.executionTime = 0;
        this.visitedVerticesNb = 0;
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
     * @return Returns the number of visited Vertices/Squares in a Graph/Maze 
     * */
    @Override
    public int getvisitedVerticesNumber(){
      return this.visitedVerticesNb;
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
