package parser;

public class ParsingContext {

    final String input;
    int cursor = 0;

    boolean failed = false;
    String lastError = "";

    public ParsingContext(String input) {
        this.input = input;
    }

    public void failure() {this.failed = true;}

    public boolean checkEOF() {
        if (this.atEnd()) {
            this.failure();
            this.setLastError("Unexpected end of input");
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

    public void setLastError(String error) {this.lastError = error;}

}
