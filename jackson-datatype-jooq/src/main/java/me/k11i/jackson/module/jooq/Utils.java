package me.k11i.jackson.module.jooq;

import java.util.Locale;

class Utils {
    private Utils() {
    }

    /**
     * This method reproduces jOOQ's {@link org.jooq.tools.StringUtils#toCamelCaseLC(String)}.
     */
    static String toCamelCase(String input) {
        input = input.toLowerCase(Locale.ENGLISH);

        boolean modified = false;
        StringBuilder result = new StringBuilder(input.length());

        boolean afterUnderscore = false;

        for (int i = 0, length = input.length(); i < length; i++) {
            char ch = input.charAt(i);
            if (ch == '_') {
                if (afterUnderscore || result.length() == 0) {
                    // Emit underscore if two or more consecutive underscores are consecutive
                    // Also emit all leading underscores
                    result.append('_');
                }

                modified = true;
                afterUnderscore = true;

            } else {
                if (afterUnderscore) {
                    ch = Character.toUpperCase(ch);
                    afterUnderscore = false;
                }
                result.append(ch);
            }
        }

        // Emit tailing underscore if exists
        if (afterUnderscore) {
            result.append('_');
        }

        if (modified) {
            return result.toString();
        } else {
            return input;
        }
    }
}
