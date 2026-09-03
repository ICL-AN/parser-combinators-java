Java Parser Combinators

A parser combinator library implemented in Java.

The project explores how larger parsers can be built by composing small reusable parsing functions rather than writing one large parser directly.

Each parser implements the TemplateParser<T> interface and operates on a shared ParsingContext, which tracks the input string, current cursor position, and parse failures.

The core parser abstraction is:

@FunctionalInterface
public interface TemplateParser<T> {
    T parse(ParsingContext ctx);
}

Parsers can then be combined using a set of higher-level combinators.

Currently implemented combinators include:

map — transforms the result of a successful parser
flatMap — chooses the next parser based on the previous parsed value
andThen — runs two parsers sequentially
or — tries an alternative parser if the first fails
maybe — makes a parser optional
many — parses zero or more occurrences
many1 — parses one or more occurrences
sepBy — parses repeated values separated by another parser
between — parses a value between opening and closing parsers
skip — discards a parser's output
lookahead — tests a parser without consuming input
notFollowedBy — ensures another parser does not immediately follow
