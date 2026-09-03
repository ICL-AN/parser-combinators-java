package parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@FunctionalInterface
public interface TemplateParser<T> {

    T parse(ParsingContext ctx);

    // Runs this parser against a fresh input string.
    default T run(String input) {
        ParsingContext ctx = new ParsingContext(input);
        return parse(ctx);
    }

    // Transforms the result of a successful parse without consuming more input.
    default <U> TemplateParser<U> map(Function<T, U> mapper) {
        return ctx -> {
            T value = parse(ctx);

            if (ctx.failed) {
                return null;
            }

            return mapper.apply(value);
        };
    }

    // Runs two parsers sequentially and returns both results.
    default <U> TemplateParser<Result<T, U>> andThen(TemplateParser<U> next) {
        return ctx -> {
            int start = ctx.cursor;

            T first = parse(ctx);

            if (ctx.failed) {
                ctx.cursor = start;
                return null;
            }

            U second = next.parse(ctx);

            if (ctx.failed) {
                ctx.cursor = start;
                return null;
            }

            return new Result<>(first, second);
        };
    }

    // Tries this parser first and backtracks before trying the alternative.
    default TemplateParser<T> or(TemplateParser<T> alternative) {
        return ctx -> {
            int start = ctx.cursor;

            T value = parse(ctx);

            if (!ctx.failed) {
                return value;
            }

            ctx.cursor = start;
            ctx.clearFailure();

            T alternativeValue = alternative.parse(ctx);

            if (ctx.failed) {
                ctx.cursor = start;
            }

            return alternativeValue;
        };
    }

    // Converts parse failure into a successful empty Optional.
    default TemplateParser<Optional<T>> maybe() {
        return ctx -> {
            int start = ctx.cursor;

            T value = parse(ctx);

            if (ctx.failed) {
                ctx.cursor = start;
                ctx.clearFailure();
                return Optional.empty();
            }

            return Optional.ofNullable(value);
        };
    }

    // Repeats this parser zero or more times.
    default TemplateParser<List<T>> many() {
        return ctx -> {
            List<T> results = new ArrayList<>();

            while (true) {
                int start = ctx.cursor;

                T value = parse(ctx);

                if (ctx.failed) {
                    ctx.cursor = start;
                    ctx.clearFailure();
                    break;
                }

                if (ctx.cursor == start) {
                    throw new IllegalStateException(
                            "many() parser succeeded without consuming input"
                    );
                }

                results.add(value);
            }

            return results;
        };
    }

    // Repeats this parser one or more times.
    default TemplateParser<List<T>> many1() {
        return ctx -> {
            int start = ctx.cursor;
            List<T> results = new ArrayList<>();

            T first = parse(ctx);

            if (ctx.failed) {
                ctx.cursor = start;
                return null;
            }

            if (ctx.cursor == start) {
                throw new IllegalStateException(
                        "many1() parser succeeded without consuming input"
                );
            }

            results.add(first);

            while (true) {
                int attempt = ctx.cursor;

                T value = parse(ctx);

                if (ctx.failed) {
                    ctx.cursor = attempt;
                    ctx.clearFailure();
                    break;
                }

                if (ctx.cursor == attempt) {
                    throw new IllegalStateException(
                            "many1() parser succeeded without consuming input"
                    );
                }

                results.add(value);
            }

            return results;
        };
    }

    // Parses zero or more items separated by the given separator.
    default TemplateParser<List<T>> sepBy(TemplateParser<?> separator) {
        return ctx -> {
            List<T> results = new ArrayList<>();
            int start = ctx.cursor;

            T first = parse(ctx);

            if (ctx.failed) {
                ctx.cursor = start;
                ctx.clearFailure();
                return results;
            }

            results.add(first);

            while (true) {
                int attempt = ctx.cursor;

                separator.parse(ctx);

                if (ctx.failed) {
                    ctx.cursor = attempt;
                    ctx.clearFailure();
                    break;
                }

                T value = parse(ctx);

                if (ctx.failed) {
                    ctx.cursor = attempt;
                    ctx.clearFailure();
                    break;
                }

                if (ctx.cursor == attempt) {
                    throw new IllegalStateException(
                            "sepBy() iteration succeeded without consuming input"
                    );
                }

                results.add(value);
            }

            return results;
        };
    }

    // Parses content surrounded by opening and closing parsers.
    default TemplateParser<T> between(
            TemplateParser<?> open,
            TemplateParser<?> close
    ) {
        return ctx -> {
            int start = ctx.cursor;

            open.parse(ctx);

            if (ctx.failed) {
                ctx.cursor = start;
                return null;
            }

            T value = parse(ctx);

            if (ctx.failed) {
                ctx.cursor = start;
                return null;
            }

            close.parse(ctx);

            if (ctx.failed) {
                ctx.cursor = start;
                return null;
            }

            return value;
        };
    }

    // Runs this parser while discarding its produced value.
    default TemplateParser<Void> skip() {
        return ctx -> {
            parse(ctx);
            return null;
        };
    }

    // Tests this parser without consuming any input.
    default TemplateParser<T> lookahead() {
        return ctx -> {
            int start = ctx.cursor;

            T value = parse(ctx);
            ctx.cursor = start;

            return value;
        };
    }

    // Succeeds only when the forbidden parser does not match immediately afterwards.
    default TemplateParser<T> notFollowedBy(TemplateParser<?> forbidden) {
        return ctx -> {
            int start = ctx.cursor;

            T value = parse(ctx);

            if (ctx.failed) {
                ctx.cursor = start;
                return null;
            }

            int afterValue = ctx.cursor;

            forbidden.parse(ctx);
            boolean matched = !ctx.failed;

            ctx.cursor = afterValue;

            if (matched) {
                ctx.fail("Unexpected following input");
                ctx.cursor = start;
                return null;
            }

            ctx.clearFailure();
            return value;
        };
    }
}