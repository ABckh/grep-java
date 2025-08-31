
public class Grep {
    public boolean match(String text, String pattern) {
        boolean hasPrefix = pattern.startsWith("^");
        boolean hasSuffix = pattern.endsWith("$");

        if (!hasPrefix) {
            pattern = ".*" + pattern;
        }

        if (!hasSuffix) {
            pattern = pattern + ".*";
        }

        NFA matcher = new NFA(pattern);
        return matcher.recognizes(text);
    }
}
