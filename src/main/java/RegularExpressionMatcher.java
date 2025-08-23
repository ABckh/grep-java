import java.util.stream.Stream;

import static java.lang.Character.isDigit;
import static java.lang.Character.isLetterOrDigit;

public class RegularExpressionMatcher {

    public boolean match(String text, String pattern) {
        //region characters groups
        if (pattern.length() == 1) {
            return text.contains(pattern);
        }

        int indexOfOpeningSquareBracket = pattern.indexOf('[');
        int indexOfClosingSquareBracket = pattern.indexOf(']');
        if (indexOfOpeningSquareBracket != -1 && indexOfClosingSquareBracket != -1) {
            String substring = pattern.substring(indexOfOpeningSquareBracket + 1, indexOfClosingSquareBracket);
            boolean isNegativeCharactersGroup = substring.startsWith("^");
            if (isNegativeCharactersGroup) {
                substring = substring.substring(1);
            }
            String chars = substring;
            Stream<String> stringStream = text.chars()
                    .distinct()
                    .mapToObj(i -> String.valueOf((char) i));
            return isNegativeCharactersGroup ?
                    stringStream.anyMatch(ch -> !chars.contains(ch)) :
                    stringStream.anyMatch(substring::contains);
        }
        //endregion

        if (pattern.charAt(0) == '^') {
            return matchHere(pattern, 1, text, 0);
        }

        int textIndex = 0;
        do {
            if (matchHere(pattern, 0, text, textIndex)) {
                return true;
            }
        } while (textIndex++ < text.length());

        return false;
    }

    private boolean matchHere(String pattern, int patternIndex, String text, int textIndex) {
        System.out.println("patternIndex:" + patternIndex);
        System.out.println("textIndex: " + textIndex);
        if (patternIndex == pattern.length()) {
            return true;
        }
        if (pattern.charAt(patternIndex) == '$' && patternIndex + 1 == pattern.length()) {
            return textIndex == text.length();
        }
        if (patternIndex + 1 < pattern.length() && pattern.charAt(patternIndex + 1) == '*') {
            return matchStar(pattern.charAt(patternIndex), pattern, patternIndex + 2, text, textIndex);
        }
        if (doesMatchShorthand('d', pattern, patternIndex, text, textIndex)) {
            return matchDigit(pattern, patternIndex, text, textIndex);
        }
        if (doesMatchShorthand('w', pattern, patternIndex, text, textIndex)) {
            return matchAlphanumeric(pattern, patternIndex, text, textIndex);
        }
        if (textIndex != text.length() && (pattern.charAt(patternIndex) == '.' || pattern.charAt(patternIndex) == text.charAt(textIndex))) {
            return matchHere(pattern, patternIndex + 1, text, textIndex + 1);
        }
        return false;
    }

    private boolean matchStar(char ch, String pattern, int patternIndex, String text, int textIndex) {
        do {
            if (matchHere(pattern, patternIndex, text, textIndex)) {
                return true;
            }
        } while (textIndex < text.length() && (text.charAt(textIndex++) == ch || ch == '.'));

        return false;
    }

    private boolean doesMatchShorthand(char ch, String pattern, int patternIndex, String text, int textIndex) {
        return textIndex != text.length() && patternIndex + 1 < pattern.length() && pattern.charAt(patternIndex) == '\\' && pattern.charAt(patternIndex + 1) == ch;
    }

    private boolean matchDigit(String pattern, int patternIndex, String text, int textIndex) {
        if (isDigit(text.charAt(textIndex))) {
            return matchHere(pattern, patternIndex + 2, text, textIndex + 1);
        }
        return false;
    }

    private boolean matchAlphanumeric(String pattern, int patternIndex, String text, int textIndex) {
        if (isLetterOrDigit(text.charAt(textIndex)) || text.charAt(textIndex) == '_') {
            return matchHere(pattern, patternIndex + 2, text, textIndex + 1);
        }
        return false;
    }
}
