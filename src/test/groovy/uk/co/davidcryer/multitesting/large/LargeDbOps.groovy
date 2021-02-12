package uk.co.davidcryer.multitesting.large

import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import uk.co.davidcryer.multitesting.generated.tables.pojos.Large

import static org.jooq.impl.DSL.trueCondition
import static uk.co.davidcryer.multitesting.generated.Tables.LARGE

@Component
class LargeDbOps {
    @Autowired
    private DSLContext dslContext

    Large get(String id) {
        dslContext
                .selectFrom(LARGE)
                .where(LARGE.ID.eq(id))
                .fetchOneInto(Large)
    }

    Large add(Large large) {
        def record = dslContext.newRecord(LARGE, large)
        record.store()
        record.into(Large)
    }

    def delete(String id) {
        dslContext
                .deleteFrom(LARGE)
                .where(LARGE.ID.eq(id))
                .execute()
    }

    def deleteAll() {
        dslContext
                .deleteFrom(LARGE)
                .where(trueCondition())
                .execute()
    }
}
