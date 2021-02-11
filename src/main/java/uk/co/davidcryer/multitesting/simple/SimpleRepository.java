package uk.co.davidcryer.multitesting.simple;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uk.co.davidcryer.multitesting.generated.Tables;
import uk.co.davidcryer.multitesting.generated.tables.pojos.Simple;
import uk.co.davidcryer.multitesting.generated.tables.records.SimpleRecord;

@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SimpleRepository {
    private final DSLContext context;

    public Simple create(Simple simple) {
        SimpleRecord record = context.newRecord(Tables.SIMPLE, simple);
        record.store();
        return record.into(Simple.class);
    }
}
