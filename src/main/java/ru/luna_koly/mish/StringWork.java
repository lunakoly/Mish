package ru.luna_koly.mish;

import net.minecraft.command.SyntaxErrorException;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.function.Function;

/**
 * Created with love by luna_koly on 08.05.2018.
 */
@SuppressWarnings("WeakerAccess")
public class StringWork {
    /**
     * Replaces all entries 'from' to 'to' in a StringBuilder inst.
     * @param builder target
     * @param from old value
     * @param to new value
     */
    @SuppressWarnings("SameParameterValue")
    public static void replaceAll(StringBuilder builder, String from, String to) {
        int index = builder.indexOf(from);
        while (index != -1) {
            builder.replace(index, index + from.length(), to);
            index += to.length(); // Move to the end of the replacement
            index = builder.indexOf(from, index);
        }
    }

    /**
     * Returns amount of spaces at the beginning of the line
     * @param line src string
     * @return count of spaces af the beginning
     */
    public static int getIndent(@Nonnull String line) {
        int indent = 0;
        while (indent < line.length() && line.charAt(indent) == ' ') indent++;
        return indent;
    }

    /**
     * Applies modulation function for all ${src} entries and
     * injects the results instead of them
     * @param line any string that might have ${} fragments
     * @param f modulation for a string fragment
     * @return the same line with all the ${} fragments modulated
     * @throws SyntaxErrorException if syntax error found
     */
    @Nonnull
    public static StringBuilder parseDollarBrackets(
            @Nonnull String line,
            @Nonnull Function<String, String> f) throws SyntaxErrorException {
        ArrayList<StringBuilder> stack = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        int it = 0;
        while (it < line.length()) {
            if (line.charAt(it) == '\\') {
                if (it < line.length() - 1) current.append(line.charAt(it + 1));
                it += 2;
                continue;
            }

            // try read command
            if (line.charAt(it) == '$' && it < line.length() - 1) {
                if (line.charAt(it + 1) == '{') {
                    stack.add(current);
                    current = new StringBuilder();
                    it++;
                }
            } else if (line.charAt(it) == '}') {
                if (stack.size() == 0) throw new SyntaxErrorException("Lonely '}' found");

                current = stack.get(stack.size() - 1)
                        .append(f.apply(current.toString()));
                stack.remove(stack.size() - 1);
            } else {
                current.append(line.charAt(it));
            }

            it++;
        }

        return current;
    }
}
