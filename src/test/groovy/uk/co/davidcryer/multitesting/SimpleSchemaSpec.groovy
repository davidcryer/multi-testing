package uk.co.davidcryer.multitesting

import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jooq.JooqTest
import org.springframework.dao.DataAccessException
import spock.lang.Specification
import uk.co.davidcryer.multitesting.generated.tables.pojos.Simple

import static uk.co.davidcryer.multitesting.generated.Tables.SIMPLE

@JooqTest
class SimpleSchemaSpec extends Specification {
    @Autowired
    private DSLContext dslContext

    def "id is generated"() {
        given:
        def simple = new Simple(null, "name")

        when:
        def record = dslContext.newRecord SIMPLE, simple
        record.store()

        then:
        record.id != null
    }

    def "inserting with id throws exception"() {
        given:
        def simple = new Simple(1, "name")

        when:
        def record = dslContext.newRecord SIMPLE, simple
        record.store()

        then:
        thrown(DataAccessException)
    }
}
