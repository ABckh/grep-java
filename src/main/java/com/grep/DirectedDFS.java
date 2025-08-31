package com.grep;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DirectedDFS {
    private final Set<State> states = new HashSet<>();
    private final Map<Integer, Integer> startToGroup;
    private final Map<Integer, Integer> endToGroup;

    public DirectedDFS(DirectedGraph graph, State state, Map<Integer, Integer> startToGroup, Map<Integer, Integer> endToGroup) {
        this.startToGroup = startToGroup;
        this.endToGroup = endToGroup;
        dfs(graph, state);
    }

    public DirectedDFS(DirectedGraph graph, Iterable<State> sources, Map<Integer, Integer> startToGroup, Map<Integer, Integer> endToGroup) {
        this.startToGroup = startToGroup;
        this.endToGroup = endToGroup;
        for (State state : sources) {
            if (!states.contains(state)) dfs(graph, state);
        }
    }

    private void dfs(DirectedGraph graph, State state) {
        states.add(state);
        for (int adjVertex : graph.adj(state.state)) {
            State adjState = new State(state);
            adjState.state = adjVertex;

            if (startToGroup.containsKey(state.state)) {
                int group = startToGroup.get(state.state);
                adjState.groupToStart.put(group, state.textPos);
            }

            if (endToGroup.containsKey(state.state)) {
                int group = endToGroup.get(state.state);
                adjState.groupToEnd.put(group, state.textPos);
            }

            if (!states.contains(adjState)) {
                states.add(adjState);
                dfs(graph, adjState);
            }
        }
    }

    public Set<State> getStates() {
        return states;
    }
}
