import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import static java.lang.Character.isDigit;
import static java.lang.Character.isLetterOrDigit;

public class NFA {
    private final char[] regexp;
    private final DirectedGraph directedGraph;
    private final int regexpLength;

    public NFA(String regexp) {
        this.regexpLength = regexp.length();
        this.regexp = regexp.toCharArray();
        this.directedGraph = buildEpsilonTransitionGraph();
    }

    public boolean recognizes(String txt) {
        Set<Integer> reachableStates = new HashSet<>();
        DirectedDFS dfs = new DirectedDFS(directedGraph, 0);

        // reachable states by epsilon transition
        for (int v = 0; v < directedGraph.getNumberOfVertices(); v++) {
            if (dfs.marked(v)) {
                reachableStates.add(v);
            }
        }

        for (int i = 0; i < txt.length(); i++) {
            Set<Integer> match = new HashSet<>();
            for (Integer state : reachableStates) {
                if (state == regexpLength) {
                    continue;
                }
                if (regexp[state] == txt.charAt(i) || regexp[state] == '.') {
                    match.add(state + 1);
                } else if (regexp[state] == '\\' && state + 1 < regexpLength && matchesShorthand(state, txt.charAt(i))) {
                    match.add(state + 2);
                }
            }

            dfs = new DirectedDFS(directedGraph, match);
            reachableStates = new HashSet<>();
            for (int v = 0; v < directedGraph.getNumberOfVertices(); v++) {
                if (dfs.marked(v)) {
                    reachableStates.add(v);
                }
            }
        }

        for (int reachableState : reachableStates) {
            if (reachableState == regexpLength) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesShorthand(Integer state, Character ch) {
        return (regexp[state + 1] == 'd' && isDigit(ch)) || (regexp[state + 1] == 'w' && (isLetterOrDigit(ch) || ch == '_'));
    }

    private DirectedGraph buildEpsilonTransitionGraph() {
        DirectedGraph graph = new DirectedGraph(regexpLength + 1);

        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < regexpLength; i++) {
            int leftParenthesis = i;

            if (regexp[i] == '(' || regexp[i] == '|') {
                stack.push(i);
            } else if (regexp[i] == ')') {
                List<Integer> ors = new ArrayList<>();
                while (!stack.isEmpty() && regexp[stack.peek()] == '|') {
                    ors.add(stack.pop());
                }

                if (!stack.isEmpty()) {
                    leftParenthesis = stack.pop();
                }

                for (int or : ors) {
                    graph.addEdge(leftParenthesis, or + 1);
                    graph.addEdge(or, i);
                }
            }

            if (i < regexpLength - 1 && regexp[i + 1] == '*') {
                graph.addEdge(leftParenthesis, i + 1);
                graph.addEdge(i + 1, leftParenthesis);
            }

            if (i < regexpLength - 1 && regexp[i + 1] == '+') {
                graph.addEdge(i + 1, leftParenthesis);
            }

            if (i < regexpLength - 1 && regexp[i + 1] == '?') {
                graph.addEdge(leftParenthesis, i + 1);
            }

            if (regexp[i] == '(' || regexp[i] == '*' || regexp[i] == ')' ||
                    regexp[i] == '+' || regexp[i] == '?' ||
                    (i == 0 && regexp[i] == '^') ||
                    (i == regexpLength - 1 || regexp[i] == '$')
            ) {
                graph.addEdge(i, i + 1);
            }
        }
        return graph;
    }
}
