package uk.co.davidcryer.multitesting.cv

import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import uk.co.davidcryer.multitesting.generated.tables.pojos.Cv

import static org.jooq.impl.DSL.inline
import static uk.co.davidcryer.multitesting.generated.Tables.CV

@Component
class CvDbOps {
    @Autowired
    private DSLContext dslContext

    def get(String id) {
        dslContext.selectFrom(CV)
                .where(CV.ID.eq(id))
                .fetchOneInto Cv
    }

    def deleteAll() {
        dslContext.delete(CV)
                .where(inline(true).eq(true))
                .execute()
    }
}
