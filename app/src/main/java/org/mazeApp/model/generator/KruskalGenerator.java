package org.mazeApp.model.generator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.mazeApp.model.Edges;
public class KruskalGenerator implements MazeGenerator {
    @Override
    public ArrayList<Edges> generate(int rows, int columns, int seed) {
        ArrayList<Edges> generationSteps = new ArrayList<>();
        ArrayList<Edges> allEdges = createGridEdges(rows, columns);
        Collections.shuffle(allEdges, new Random(seed));
        
        int totalVertices = rows * columns;
        int[] parent = new int[totalVertices];
        for (int i = 0; i < totalVertices; i++) {
            parent[i] = i;
        }

        for (Edges edge : allEdges) {
            int source = edge.getSource();
            int destination = edge.getDestination();

            int sourceRoot = find(parent, source);
            int destRoot = find(parent, destination);

            if (sourceRoot != destRoot) {
                generationSteps.add(new Edges(source, destination));
                union(parent, sourceRoot, destRoot);
            }
        }
        
        return generationSteps;
    }
    
    @Override
    public String getName() {
        return "Kruskal";
    }
    
    private ArrayList<Edges> createGridEdges(int rows, int columns) {
        ArrayList<Edges> edges = new ArrayList<>();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                int current = row * columns + col;

                if (col < columns - 1) {
                    edges.add(new Edges(current, current + 1));
                }
                if (row < rows - 1) {
                    edges.add(new Edges(current, current + columns));
                }
            }
        }
        return edges;
    }
    
    private int find(int[] parent, int vertex) {
        if (parent[vertex] != vertex) {
            parent[vertex] = find(parent, parent[vertex]);
        }
        return parent[vertex];
    }
    
    private void union(int[] parent, int x, int y) {
        parent[x] = y;
    }
}