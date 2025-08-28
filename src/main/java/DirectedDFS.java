
public class DirectedDFS {
    private final boolean[] marked;     // marked[v] = true if v is reachable from source(s)

    public DirectedDFS(DirectedGraph graph, int s) {
        marked = new boolean[graph.getNumberOfVertices()];
        dfs(graph, s);
    }

    public DirectedDFS(DirectedGraph graph, Iterable<Integer> sources) {
        marked = new boolean[graph.getNumberOfVertices()];
        for (int v : sources) {
            if (!marked[v]) dfs(graph, v);
        }
    }

    private void dfs(DirectedGraph graph, int vertex) {
        marked[vertex] = true;
        for (int adjVertex : graph.adj(vertex)) {
            if (!marked[adjVertex]) dfs(graph, adjVertex);
        }
    }

    public boolean marked(int v) {
        return marked[v];
    }
}
