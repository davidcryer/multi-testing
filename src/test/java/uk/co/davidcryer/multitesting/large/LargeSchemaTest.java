package uk.co.davidcryer.multitesting.large;

import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;

import java.util.UUID;

import static uk.co.davidcryer.multitesting.generated.Tables.LARGE;

@JooqTest
public class LargeSchemaTest {
    @Autowired
    private DSLContext dslContext;

    @Test
    public void insert_acceptsRandomUUID() {
        var record = dslContext.newRecord(LARGE);
        record.setId(UUID.randomUUID().toString());
        record.store();
    }
}
