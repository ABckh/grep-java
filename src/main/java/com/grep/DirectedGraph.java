package com.grep;

import java.util.HashSet;

public class DirectedGraph {
    private final int numberOfVertices;        // number of vertices in this com.grep.DirectedGraph
    private final HashSet<Integer>[] adj;      // adj[v] = adjacency list for vertex v (vertices pointing from V)

    public DirectedGraph(int numberOfVertices) {
        if (numberOfVertices < 0) {
            throw new IllegalArgumentException("Number of vertices in a com.grep.DirectedGraph must be non-negative");
        }
        this.numberOfVertices = numberOfVertices;
        adj = new HashSet[numberOfVertices];
        for (int v = 0; v < numberOfVertices; v++) {
            adj[v] = new HashSet<>();
        }
    }

    public int getNumberOfVertices() {
        return numberOfVertices;
    }

    public void addEdge(int from, int to) {
        adj[from].add(to);
    }

    public Iterable<Integer> adj(int v) {
        return adj[v];
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int v = 0; v < getNumberOfVertices(); v++) {
            s.append(String.format("%d: ", v));
            for (int w : adj[v]) {
                s.append(String.format("%d ", w));
            }
            s.append(System.lineSeparator());
        }
        return s.toString();
    }
}
