import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import static java.lang.Character.getNumericValue;
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
                int pos = reachableState.state;
                if (pos == regexpLength || reachableState.textPos >= txt.length()) {
                    continue;
                }

                char ch = txt.charAt(reachableState.textPos);

                if (regexp[pos] == ch || regexp[pos] == '.') {
                    State matchedState = new State(reachableState);
                    matchedState.state = pos + 1;
                    matchedState.textPos = reachableState.textPos + 1;
                    match.add(matchedState);
                } else if (regexp[pos] == '\\' && pos + 1 < regexpLength) {
                    char nextCh = regexp[pos + 1];
                    if (matchesShorthand(pos, ch)) {
                        State matchedState = new State(reachableState);
                        matchedState.state = pos + 2;
                        matchedState.textPos = reachableState.textPos + 1;
                        match.add(matchedState);
                    } else if (isDigit(nextCh)) {
                        // backreferences
                        int group = getNumericValue(nextCh);
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
                            matchedState.state = pos + 2;
                            matchedState.textPos = reachableState.textPos + len;
                            match.add(matchedState);
                        }
                    }
                } else if (regexp[pos] == '[') {
                    int closing = pos + 1;
                    closing = findClosing(closing);
                    if (closing == regexpLength) {
                        continue;
                    }
                    boolean negated = (pos + 1 < regexpLength && regexp[pos + 1] == '^');
                    int start = negated ? pos + 2 : pos + 1;
                    Set<Character> charSet = new HashSet<>();
                    for (int i = start; i < closing; i++) {
                        charSet.add(regexp[i]);
                    }
                    boolean doesMatch = charSet.contains(ch);
                    if (negated) {
                        doesMatch = !doesMatch;
                    }
                    if (doesMatch) {
                        State matchedState = new State(reachableState);
                        matchedState.state = closing + 1;
                        matchedState.textPos = reachableState.textPos + 1;
                        match.add(matchedState);
                    }
                }
            }
            dfs = new DirectedDFS(directedGraph, match, groupStartPosToNum, groupEndPosToNum);
            reachableStates = dfs.getStates();
        }

        return false;
    }

    private DirectedGraph buildEpsilonTransitionGraph() {
        DirectedGraph graph = new DirectedGraph(regexpLength + 1);

        int groupCounter = 0;
        Stack<Integer> groupStack = new Stack<>();
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < regexpLength; i++) {
            int left = i;
            if (i > 0 && regexp[i - 1] == '\\' && isShorthand(regexp[i])) {
                left = i - 1;
            } else if (regexp[i] == '[') {
                int closing = i + 1;
                closing = findClosing(closing);
                if (closing < regexpLength) {
                    i = closing;
                }
            }

            if (regexp[i] == '(') {
                stack.push(i);
                groupCounter++;
                groupStartPosToNum.put(i, groupCounter);
                groupStack.push(groupCounter);
            } else if (regexp[i] == '|') {
                stack.push(i);
            } else if (regexp[i] == ')') {
                List<Integer> ors = new ArrayList<>();
                while (!stack.isEmpty() && regexp[stack.peek()] == '|') {
                    ors.add(stack.pop());
                }

                if (!stack.isEmpty()) {
                    left = stack.pop();
                    int group = groupStack.pop();
                    groupEndPosToNum.put(i, group);
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
                    (i == regexpLength - 1 && regexp[i] == '$')) {
                graph.addEdge(i, i + 1);
            }
        }

        return graph;
    }

    private boolean matchesShorthand(int state, char ch) {
        return (regexp[state + 1] == 'd' && isDigit(ch)) || (regexp[state + 1] == 'w' && (isLetterOrDigit(ch) || ch == '_'));
    }

    private boolean isShorthand(char c) {
        return c == 'd' || c == 'w';
    }

    private int findClosing(int closing) {
        while (closing < regexpLength && regexp[closing] != ']') {
            closing++;
        }
        return closing;
    }
}
