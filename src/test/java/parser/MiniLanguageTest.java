package parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;

class MiniLanguageTest {

    private Map<String, Map<String, String>> objects;

    @BeforeEach
    void setUp() {
        objects = new HashMap<>();
        objects.put("logins", new HashMap<>());
        objects.put("settings", new HashMap<>());
    }

    @Test
    void assignmentStoresValue() {
        /*
         * Eventually build a parser for:
         *     logins[username] = coder
         *
         * Parse object name, key and value, then map the successful parse into:
         *     objects.get(objectName).put(key, value)
         */
        fail("Stage 2");
    }

    @Test
    void deleteRemovesValue() {
        objects.get("logins").put("username", "coder");

        /*
         * Eventually build a parser for:
         *     delete logins[username]
         *
         * Map the successful parse into removal from objects.
         */
        fail("Stage 2");
    }

    @Test
    void multipleStatementsModifySameObject() {
        /*
         * Eventually parse something like:
         *     logins[username] = coder;
         *     logins[token] = abc;
         *     delete logins[token];
         *
         * Expected final state:
         *     username -> coder
         *     token    -> absent
         */
        fail("Stage 2");
    }
}
