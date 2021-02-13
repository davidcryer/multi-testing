package uk.co.davidcryer.multitesting.cucumber;

import uk.co.davidcryer.multitesting.generated.tables.pojos.Simple;
import uk.co.davidcryer.multitesting.simple.SimpleRequest;

public class SimpleData {

    static SimpleRequest request() {
        return new SimpleRequest(null, "test-name");
    }

    static Simple entity() {
        return new Simple(null, "test-name");
    }
}
