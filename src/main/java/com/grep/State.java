package com.grep;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class State {
    int state;
    int textPos;
    Map<Integer, Integer> groupToStart = new HashMap<>();
    Map<Integer, Integer> groupToEnd = new HashMap<>();

    State(int state, int textPos) {
        this.state = state;
        this.textPos = textPos;
    }

    State(State other) {
        state = other.state;
        textPos = other.textPos;
        groupToStart = new HashMap<>(other.groupToStart);
        groupToEnd = new HashMap<>(other.groupToEnd);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return this.state == state.state && textPos == state.textPos && groupToStart.equals(state.groupToStart) && groupToEnd.equals(state.groupToEnd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, textPos, groupToStart, groupToEnd);
    }
}
