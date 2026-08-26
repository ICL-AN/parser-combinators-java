package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParsingContext {

    final String input;
    int cursor = 0;

    boolean failed = false;
    String lastError = "";

    // Records every failure observed while exploring different parse paths.
    final Map<Integer, List<String>> errorHistory = new HashMap<>();

    // Tracks the deepest point at which any parser has failed.
    int furthestFailure = -1;

    public ParsingContext(String input) {
        this.input = input;
    }

    public void fail(String error) {
        this.failed = true;
        this.lastError = error;

        errorHistory
                .computeIfAbsent(cursor, k -> new ArrayList<>())
                .add(error);

        furthestFailure = Math.max(furthestFailure, cursor);
    }

    public void clearFailure() {
        this.failed = false;
        this.lastError = "";
    }

    public boolean checkEOF() {
        if (this.atEnd()) {
            this.fail("Unexpected end of input");
            return true;
        }
        return false;
    }

    private boolean atEnd() {
        return cursor >= input.length();
    }

    public int remaining() {
        return input.length() - cursor;
    }
}