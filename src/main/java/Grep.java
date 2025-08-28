import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Grep {
    public boolean match(String text, String pattern) {
        pattern = preProcess(pattern);
        boolean hasPrefix = pattern.startsWith("^") || pattern.startsWith("[");
        boolean hasSuffix = pattern.endsWith("$") || pattern.endsWith("]");

        if (!hasPrefix) {
            pattern = ".*" + pattern;
        }

        if (!hasSuffix) {
            pattern = pattern + ".*";
        }

        NFA matcher = new NFA(pattern);
        return matcher.recognizes(text);
    }

    private String preProcess(String pattern) {
        StringBuilder sb = new StringBuilder();

        int i = 0;
        while (i < pattern.length()) {
            if (pattern.charAt(i) == '[') {
                int closing = pattern.indexOf(']', i);
                if (closing == -1) {
                    sb.append(pattern.charAt(i));
                    i++;
                    continue;
                }

                boolean isNegated = i + 1 < pattern.length() && pattern.charAt(i + 1) == '^';
                int start = isNegated ? i + 2 : i + 1;

                IntStream chars = pattern.substring(start, closing).chars();

                if (isNegated) {
                    Set<Integer> charSet = chars
                            .boxed()
                            .collect(Collectors.toSet());
                    sb.append("(")
                            .append(IntStream.range(48, 122)
                                    .filter(ch -> !charSet.contains(ch) && (int) '?' != ch)
                                    .mapToObj(Character::toString)
                                    .collect(Collectors.joining("|")))
                            .append(")");
                } else {
                    sb.append("(")
                            .append(chars.mapToObj(Character::toString)
                                    .collect(Collectors.joining("|")))
                            .append(")");
                }

                i = closing;
            } else {
                sb.append(pattern.charAt(i));
            }
            i++;
        }
        return sb.toString();
    }
}
