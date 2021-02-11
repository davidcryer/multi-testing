package uk.co.davidcryer.multitesting.simple;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uk.co.davidcryer.multitesting.generated.Tables;
import uk.co.davidcryer.multitesting.generated.tables.pojos.Simple;
import uk.co.davidcryer.multitesting.generated.tables.records.SimpleRecord;

import static uk.co.davidcryer.multitesting.generated.Tables.SIMPLE;

@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SimpleRepository {
    private final DSLContext dslContext;

    public Simple create(Simple simple) {
        SimpleRecord record = dslContext.newRecord(SIMPLE, simple);
        record.store();
        return record.into(Simple.class);
    }

    public Simple get(Integer id) {
        return dslContext
                .selectFrom(SIMPLE)
                .where(SIMPLE.ID.eq(id))
                .fetchOneInto(Simple.class);
    }
}
