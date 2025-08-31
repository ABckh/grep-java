package com.grep;

public class Grep {
    private final NFA matcher;

    public Grep(String input) {
        String pattern = getPattern(input);
        this.matcher = new NFA(pattern);
    }

    private String getPattern(String input) {
        boolean hasPrefix = input.startsWith("^");
        boolean hasSuffix = input.endsWith("$");

        if (!hasPrefix) {
            input = ".*" + input;
        }

        if (!hasSuffix) {
            input = input + ".*";
        }

        return input;
    }

    public boolean match(String text) {
        return matcher.recognizes(text);
    }
}
