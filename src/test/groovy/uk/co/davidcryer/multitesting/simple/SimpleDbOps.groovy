package uk.co.davidcryer.multitesting.simple

import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import uk.co.davidcryer.multitesting.generated.tables.pojos.Simple

import static org.jooq.impl.DSL.trueCondition
import static uk.co.davidcryer.multitesting.generated.tables.Simple.SIMPLE

@Component
class SimpleDbOps {
    @Autowired
    private DSLContext dslContext

    Simple get(Integer id) {
        dslContext
                .selectFrom(SIMPLE)
                .where(SIMPLE.ID.eq(id))
                .fetchOneInto(Simple)
    }

    Simple insert(Simple simple) {
        def record = dslContext.newRecord(SIMPLE, simple)
        record.store()
        record.into(Simple)
    }

    def delete(Integer id) {
        dslContext
                .deleteFrom(SIMPLE)
                .where(SIMPLE.ID.eq(id))
                .execute()
    }

    def deleteAll() {
        dslContext
                .deleteFrom(SIMPLE)
                .where(trueCondition())
    }
}
