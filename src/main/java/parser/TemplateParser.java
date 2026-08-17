package parser;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@FunctionalInterface
public interface TemplateParser<T> {

    T parse(ParsingContext ctx);

    default T run(String input) {
        ParsingContext ctx = new ParsingContext(input);
        return parse(ctx);
    }

    /**
     * Run this parser and, if it succeeds, transform its produced value using mapper.
     * Mapping must not itself consume input or alter where the parser cursor has reached.
     * If this parser fails, propagate that failure and do not call the mapping function.
     */
    default <U> TemplateParser<U> map(Function<T, U> mapper) {
        throw new UnsupportedOperationException("TODO: map");
    }

    /**
     * Run this parser first and then run next starting exactly where this parser finished.
     * The combined parser succeeds only when both parsers succeed, returning both results.
     * Decide what state should remain if the first succeeds but the second parser fails.
     */
    default <U> TemplateParser<Result<T, U>> andThen(TemplateParser<U> next) {
        throw new UnsupportedOperationException("TODO: andThen");
    }

    /**
     * Try this parser first; only if it fails should the alternative parser be attempted.
     * The alternative must see the same input position that this parser originally started at.
     * Therefore failure of the first branch requires carefully restoring its consumed state.
     */
    default TemplateParser<T> or(TemplateParser<T> alternative) {
        throw new UnsupportedOperationException("TODO: or");
    }

    /**
     * Attempt this parser but turn ordinary parse failure into a successful empty Optional.
     * A failed optional parse must consume absolutely no input and must clear its failure state.
     * A successful parse keeps its consumed input and returns Optional.of(the parsed value).
     */
    default TemplateParser<Optional<T>> maybe() {
        throw new UnsupportedOperationException("TODO: maybe");
    }

    /**
     * Repeatedly run this parser until the next attempt fails, collecting every successful result.
     * The failed final attempt is only the signal to stop, so its cursor changes must be rolled back.
     * Guard against a parser that succeeds without advancing the cursor or this loop never terminates.
     */
    default TemplateParser<List<T>> many() {
        throw new UnsupportedOperationException("TODO: many");
    }

    /**
     * Behave like many(), except the parser must successfully match at least one occurrence.
     * Failure on the very first attempt means many1 itself fails rather than returning an empty list.
     * After one success, later failure simply terminates repetition exactly as it does for many().
     */
    default TemplateParser<List<T>> many1() {
        throw new UnsupportedOperationException("TODO: many1");
    }

    /**
     * Parse zero or more occurrences of this parser with separator appearing only between items.
     * A separator must never be accepted on its own without a following successfully parsed item.
     * Think carefully about rollback when a separator succeeds but the item after it then fails.
     */
    default TemplateParser<List<T>> sepBy(TemplateParser<?> separator) {
        throw new UnsupportedOperationException("TODO: sepBy");
    }

    /**
     * Require open first, then this parser's content, then close, returning only the content value.
     * The delimiters affect whether parsing succeeds and consume input but their values are discarded.
     * Treat the entire operation as one parser and decide how partial failure should affect the cursor.
     */
    default TemplateParser<T> between(
            TemplateParser<?> open,
            TemplateParser<?> close
    ) {
        throw new UnsupportedOperationException("TODO: between");
    }

    /**
     * Run this parser normally but deliberately discard the value it produces when successful.
     * Input consumption and parse failure behave exactly as they did for the original parser.
     * The resulting Void parser is useful for punctuation, whitespace and other structural syntax.
     */
    default TemplateParser<Void> skip() {
        throw new UnsupportedOperationException("TODO: skip");
    }

    /**
     * Run this parser only to inspect whether it matches at the current position without consuming it.
     * Return the parser's value on success, but restore the cursor to its original position afterwards.
     * Failure should also leave the cursor untouched while still reporting that the lookahead failed.
     */
    default TemplateParser<T> lookahead() {
        throw new UnsupportedOperationException("TODO: lookahead");
    }

    /**
     * First parse this parser, then check that forbidden does NOT match immediately afterwards.
     * The forbidden parser is only a probe and therefore must never consume input itself.
     * If forbidden does match, this whole parser fails; useful for rules such as keyword boundaries.
     */
    default TemplateParser<T> notFollowedBy(TemplateParser<?> forbidden) {
        throw new UnsupportedOperationException("TODO: notFollowedBy");
    }
}
