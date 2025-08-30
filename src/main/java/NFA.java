import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import static java.lang.Character.isDigit;
import static java.lang.Character.isLetterOrDigit;

public class NFA {
    private final char[] regexp;
    private final DirectedGraph directedGraph;
    private final int regexpLength;
    private final Map<Integer, Integer> groupStartPosToNum = new HashMap<>();
    private final Map<Integer, Integer> groupEndPosToNum = new HashMap<>();

    public NFA(String regexp) {
        this.regexpLength = regexp.length();
        this.regexp = regexp.toCharArray();
        this.directedGraph = buildEpsilonTransitionGraph();
    }

    public boolean recognizes(String txt) {
        DirectedDFS dfs = new DirectedDFS(directedGraph, new State(0, 0), groupStartPosToNum, groupEndPosToNum);

        // reachable states by epsilon transition
        Set<State> reachableStates = dfs.getStates();
        while (!reachableStates.isEmpty()) {
            for (State state : reachableStates) {
                if (state.state == regexpLength && state.textPos == txt.length()) {
                    return true;
                }
            }

            Set<State> match = new HashSet<>();
            for (State reachableState : reachableStates) {
                int state = reachableState.state;
                if (state == regexpLength || reachableState.textPos >= txt.length()) {
                    continue;
                }

                char ch = txt.charAt(reachableState.textPos);

                if (regexp[state] == ch || regexp[state] == '.') {
                    State matchedState = new State(reachableState);
                    matchedState.state = state + 1;
                    matchedState.textPos = reachableState.textPos + 1;
                    match.add(matchedState);
                } else if (regexp[state] == '\\' && state + 1 < regexpLength) {
                    char nextCh = regexp[state + 1];
                    if (matchesShorthand(state, ch)) {
                        State matchedState = new State(reachableState);
                        matchedState.state = state + 2;
                        matchedState.textPos = reachableState.textPos + 1;
                        match.add(matchedState);
                    } else if (Character.isDigit(nextCh)) {
                        // backreferences
                        int group = Character.getNumericValue(nextCh);
                        if (!reachableState.groupToStart.containsKey(group) || !reachableState.groupToEnd.containsKey(group)) {
                            continue;
                        }

                        int groupStart = reachableState.groupToStart.get(group);
                        int groupEnd = reachableState.groupToEnd.get(group);
                        int len = groupEnd - groupStart;

                        if (reachableState.textPos + len > txt.length()) {
                            continue;
                        }

                        boolean matches = true;
                        for (int i = 0; i < len; i++) {
                            if (txt.charAt(reachableState.textPos + i) != txt.charAt(groupStart + i)) {
                                matches = false;
                                break;
                            }
                        }
                        if (matches) {
                            State matchedState = new State(reachableState);
                            matchedState.state = state + 2;
                            matchedState.textPos = reachableState.textPos + len;
                            match.add(matchedState);
                        }
                    }
                }
            }
            dfs = new DirectedDFS(directedGraph, match, groupStartPosToNum, groupEndPosToNum);
            reachableStates = dfs.getStates();
        }

        return false;
    }

    private boolean matchesShorthand(Integer state, Character ch) {
        return (regexp[state + 1] == 'd' && isDigit(ch)) || (regexp[state + 1] == 'w' && (isLetterOrDigit(ch) || ch == '_'));
    }

    private DirectedGraph buildEpsilonTransitionGraph() {
        DirectedGraph graph = new DirectedGraph(regexpLength + 1);

        int groupCounter = 0;
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < regexpLength; i++) {
            int left = i;
            if (i > 0 && regexp[i - 1] == '\\' && isShorthand(regexp[i])) {
                left = i - 1;
            }

            if (regexp[i] == '(') {
                stack.push(i);
                groupCounter++;
                groupStartPosToNum.put(i, groupCounter);
            } else if (regexp[i] == '|') {
                stack.push(i);
            } else if (regexp[i] == ')') {
                List<Integer> ors = new ArrayList<>();
                while (!stack.isEmpty() && regexp[stack.peek()] == '|') {
                    ors.add(stack.pop());
                }

                if (!stack.isEmpty()) {
                    left = stack.pop();
                    groupEndPosToNum.put(i, groupCounter);
                    groupCounter--;
                }

                for (int or : ors) {
                    graph.addEdge(left, or + 1);
                    graph.addEdge(or, i);
                }
            }

            if (i < regexpLength - 1 && regexp[i + 1] == '*') {
                graph.addEdge(left, i + 1);
                graph.addEdge(i + 1, left);
            }

            if (i < regexpLength - 1 && regexp[i + 1] == '+') {
                graph.addEdge(i + 1, left);
            }

            if (i < regexpLength - 1 && regexp[i + 1] == '?') {
                graph.addEdge(left, i + 1);
            }

            if (regexp[i] == '(' || regexp[i] == '*' || regexp[i] == ')' ||
                    regexp[i] == '+' || regexp[i] == '?' ||
                    (i == 0 && regexp[i] == '^') ||
                    (i == regexpLength - 1 && regexp[i] == '$')
            ) {
                graph.addEdge(i, i + 1);
            }
        }
        return graph;
    }

    private boolean isShorthand(char c) {
        return c == 'd' || c == 'w';
    }
}
