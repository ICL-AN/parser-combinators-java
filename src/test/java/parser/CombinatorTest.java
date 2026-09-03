package parser;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CombinatorTest {

    @Test
    void mapTransformsValue() {
        TemplateParser<Integer> parser = Parsers.literal("123").map(Integer::parseInt);
        ParsingContext ctx = new ParsingContext("123");
        Integer result = parser.parse(ctx);

        assertFalse(ctx.failed);
        assertEquals(123, result);
        assertEquals(3, ctx.cursor);
    }

    @Test
    void andThenRunsSequentially() {
        TemplateParser<Result<String, String>> parser =
                Parsers.literal("hello").andThen(Parsers.literal("world"));

        ParsingContext ctx = new ParsingContext("helloworld");
        Result<String, String> result = parser.parse(ctx);

        assertFalse(ctx.failed);
        assertEquals("hello", result.first());
        assertEquals("world", result.second());
        assertEquals(10, ctx.cursor);
    }

    @Test
    void flatMapUsesParsedValueToChooseNextParser() {
        TemplateParser<Character> digit =
                Parsers.satisfy(Character::isDigit);

        TemplateParser<String> parser =
                digit.flatMap(d -> Parsers.literal(String.valueOf(d).repeat(2)));

        assertEquals("33", parser.run("333"));
    }

    @Test
    void orUsesSecondParserAfterFirstFails() {
        TemplateParser<String> parser =
                Parsers.literal("hello").or(Parsers.literal("help"));

        ParsingContext ctx = new ParsingContext("help");
        String result = parser.parse(ctx);

        assertFalse(ctx.failed);
        assertEquals("help", result);
        assertEquals(4, ctx.cursor);
    }

    @Test
    void orRestoresCursorBeforeAlternative() {
        TemplateParser<String> badParser = ctx -> {
            ctx.cursor += 2;
            ctx.failed = true;
            ctx.lastError = "deliberate failure";
            return null;
        };

        TemplateParser<String> parser = badParser.or(Parsers.literal("hello"));
        ParsingContext ctx = new ParsingContext("hello");
        String result = parser.parse(ctx);

        assertFalse(ctx.failed);
        assertEquals("hello", result);
        assertEquals(5, ctx.cursor);
    }

    @Test
    void maybeReturnsPresentValueWhenSuccessful() {
        TemplateParser<Optional<String>> parser = Parsers.literal("hello").maybe();
        ParsingContext ctx = new ParsingContext("hello");
        Optional<String> result = parser.parse(ctx);

        assertFalse(ctx.failed);
        assertEquals(Optional.of("hello"), result);
        assertEquals(5, ctx.cursor);
    }

    @Test
    void maybeReturnsEmptyWithoutConsumingOnFailure() {
        TemplateParser<Optional<String>> parser = Parsers.literal("hello").maybe();
        ParsingContext ctx = new ParsingContext("world");
        Optional<String> result = parser.parse(ctx);

        assertFalse(ctx.failed);
        assertEquals(Optional.empty(), result);
        assertEquals(0, ctx.cursor);
    }

    @Test
    void manyCollectsRepeatedMatches() {
        TemplateParser<List<Character>> digits = Parsers.satisfy(Character::isDigit).many();
        ParsingContext ctx = new ParsingContext("123abc");
        List<Character> result = digits.parse(ctx);

        assertFalse(ctx.failed);
        assertEquals(List.of('1', '2', '3'), result);
        assertEquals(3, ctx.cursor);
    }

    @Test
    void manyAllowsZeroMatches() {
        TemplateParser<List<Character>> digits = Parsers.satisfy(Character::isDigit).many();
        ParsingContext ctx = new ParsingContext("abc");
        List<Character> result = digits.parse(ctx);

        assertFalse(ctx.failed);
        assertTrue(result.isEmpty());
        assertEquals(0, ctx.cursor);
    }

    @Test
    void many1RequiresAtLeastOneMatch() {
        TemplateParser<List<Character>> digits = Parsers.satisfy(Character::isDigit).many1();
        ParsingContext ctx = new ParsingContext("abc");
        digits.parse(ctx);

        assertTrue(ctx.failed);
        assertEquals(0, ctx.cursor);
    }

    @Test
    void sepByParsesCommaSeparatedValues() {
        TemplateParser<String> identifier =
                Parsers.satisfy(Character::isLetter)
                        .many1()
                        .map(chars -> {
                            StringBuilder builder = new StringBuilder();
                            chars.forEach(builder::append);
                            return builder.toString();
                        });

        TemplateParser<List<String>> parser = identifier.sepBy(Parsers.literal(","));
        ParsingContext ctx = new ParsingContext("alice,bob,charlie");
        List<String> result = parser.parse(ctx);

        assertFalse(ctx.failed);
        assertEquals(List.of("alice", "bob", "charlie"), result);
        assertEquals(17, ctx.cursor);
    }

    @Test
    void betweenReturnsOnlyInnerValue() {
        TemplateParser<String> parser = Parsers.literal("hello")
                .between(Parsers.literal("["), Parsers.literal("]"));

        ParsingContext ctx = new ParsingContext("[hello]");
        String result = parser.parse(ctx);

        assertFalse(ctx.failed);
        assertEquals("hello", result);
        assertEquals(7, ctx.cursor);
    }

    @Test
    void skipConsumesButDiscardsResult() {
        TemplateParser<Void> parser = Parsers.literal("   ").skip();
        ParsingContext ctx = new ParsingContext("   hello");
        Void result = parser.parse(ctx);

        assertFalse(ctx.failed);
        assertNull(result);
        assertEquals(3, ctx.cursor);
    }

    @Test
    void lookaheadDoesNotConsumeInput() {
        TemplateParser<String> parser = Parsers.literal("hello").lookahead();
        ParsingContext ctx = new ParsingContext("hello world");
        String result = parser.parse(ctx);

        assertFalse(ctx.failed);
        assertEquals("hello", result);
        assertEquals(0, ctx.cursor);
    }

    @Test
    void notFollowedByAcceptsKeywordBoundary() {
        TemplateParser<String> keyword = Parsers.literal("if")
                .notFollowedBy(Parsers.satisfy(Character::isLetterOrDigit));

        ParsingContext ctx = new ParsingContext("if ");
        String result = keyword.parse(ctx);

        assertFalse(ctx.failed);
        assertEquals("if", result);
        assertEquals(2, ctx.cursor);
    }

    @Test
    void notFollowedByRejectsLongerIdentifier() {
        TemplateParser<String> keyword = Parsers.literal("if")
                .notFollowedBy(Parsers.satisfy(Character::isLetterOrDigit));

        ParsingContext ctx = new ParsingContext("iffy");
        keyword.parse(ctx);

        assertTrue(ctx.failed);
    }
}
