package parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PrimitiveParserTest {

    @Test
    void satisfyMatchesDigit() {
        ParsingContext ctx = new ParsingContext("7abc");
        Character result = Parsers.satisfy(Character::isDigit).parse(ctx);

        assertFalse(ctx.failed);
        assertEquals('7', result);
        assertEquals(1, ctx.cursor);
    }

    @Test
    void satisfyMatchesAlphanumericFail() {
        ParsingContext ctx = new ParsingContext("#a7");
        Character result = Parsers.satisfy(Character::isLetterOrDigit).parse(ctx);

        assertTrue(ctx.failed);
        assertNull(result);
        assertEquals(0, ctx.cursor);
    }

    @Test
    void satisfyMatchesAlphanumeric() {
        ParsingContext ctx = new ParsingContext("a7");
        Character result = Parsers.satisfy(Character::isLetterOrDigit).parse(ctx);

        assertFalse(ctx.failed);
        assertEquals('a', result);
        assertEquals(1, ctx.cursor);
    }



    @Test
    void satisfyDoesNotConsumeOnFailure() {
        ParsingContext ctx = new ParsingContext("x123");
        Parsers.satisfy(Character::isDigit).parse(ctx);

        assertTrue(ctx.failed);
        assertEquals(0, ctx.cursor);
    }

    @Test
    void literalMatchesExactText() {
        ParsingContext ctx = new ParsingContext("hello world");
        String result = Parsers.literal("hello").parse(ctx);

        assertFalse(ctx.failed);
        assertEquals("hello", result);
        assertEquals(5, ctx.cursor);
    }

    @Test
    void literalDoesNotAcceptPartialMatch() {
        ParsingContext ctx = new ParsingContext("help");
        Parsers.literal("hello").parse(ctx);

        assertTrue(ctx.failed);
        assertEquals(0, ctx.cursor);
    }

    @Test
    void eofSucceedsAtEnd() {
        ParsingContext ctx = new ParsingContext("abc");
        ctx.cursor = 3;
        Parsers.eof().parse(ctx);

        assertFalse(ctx.failed);
        assertEquals(3, ctx.cursor);
    }

    @Test
    void eofFailsBeforeEnd() {
        ParsingContext ctx = new ParsingContext("abc");
        ctx.cursor = 2;
        Parsers.eof().parse(ctx);

        assertTrue(ctx.failed);
        assertEquals(2, ctx.cursor);
    }
}
