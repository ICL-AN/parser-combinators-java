package parser;

import java.util.function.Predicate;

public final class Parsers {

    private Parsers() {}

    /**
     * Inspect exactly one character at the current cursor and test it against predicate.
     * On success consume that character and return it; on failure consume nothing.
     * Failure should mark the context and provide a useful lastError message.
     */
    public static TemplateParser<Character> satisfy(
            Predicate<Character> predicate
    ) {
        return ctx -> {
            if (ctx.checkEOF()) return null;
            char c = ctx.input.charAt(ctx.cursor);
            if (predicate.test(c)) {
                ctx.cursor++;
                return c;
            } else {
                ctx.fail("Predicate failure at " + ctx.cursor);
                return null;
            }
        };
    }



    /**
     * Match the exact supplied string beginning at the context's current cursor position.
     * Success consumes the whole literal and returns it; partial matches must not be accepted.
     * Failure must leave the cursor where it was before the literal parser began.
     */
    public static TemplateParser<String> literal(String expected) {
        return ctx -> {
            int start = ctx.cursor;
            int index = 0;

            while (index < expected.length()
                    && start + index < ctx.input.length()
                    && ctx.input.charAt(start + index) == expected.charAt(index)) {
                index++;
            }

            if (index == expected.length()) {
                ctx.cursor += expected.length();
                return expected;
            }

            if (start + index >= ctx.input.length()) {
                ctx.fail("Unexpected end of input while matching \"" + expected + "\"");
            } else {
                ctx.fail("Literal mismatch at position " + (start + index));
            }

            return null;
        };
    }

    /**
     * Succeed only when the cursor is positioned exactly at the end of the input string.
     * EOF consumes no input and exists mainly to reject otherwise-valid prefixes with trailing text.
     * If characters remain, mark the parse as failed and describe the unexpected remaining input.
     */
    public static TemplateParser<Void> eof() {
        return ctx -> {
            if (ctx.cursor < ctx.input.length()) {
                ctx.fail("Trailing input begins with '" + ctx.input.charAt(ctx.cursor)
                        + "' at position " + ctx.cursor);
            }
            return null;
        };
    }
}
