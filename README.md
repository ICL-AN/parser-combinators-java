# Java Parser Combinators

A **parser combinator library implemented in Java**.

The project explores how larger parsers can be built by composing small, reusable parsing functions rather than implementing a parser as one large piece of logic.

## Core Abstraction

Each parser implements the `TemplateParser<T>` interface and operates on a shared `ParsingContext`, which tracks the input string, current cursor position, and parse failures.

```java
@FunctionalInterface
public interface TemplateParser<T> {
    T parse(ParsingContext ctx);
}
```

Parsers can then be composed using higher-level combinators to build more complex parsing behaviour.

## Combinators

Currently implemented combinators include:

* `map` — transforms the result of a successful parser.
* `flatMap` — chooses the next parser based on the previously parsed value.
* `andThen` — runs two parsers sequentially.
* `or` — tries an alternative parser if the first parser fails.
* `maybe` — makes a parser optional.
* `many` — parses zero or more occurrences.
* `many1` — parses one or more occurrences.
* `sepBy` — parses repeated values separated by another parser.
* `between` — parses a value between opening and closing parsers.
* `skip` — runs a parser but discards its output.
* `lookahead` — tests a parser without consuming input.
* `notFollowedBy` — succeeds only if another parser does not immediately follow.
