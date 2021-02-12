package uk.co.davidcryer.multitesting.simple


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jooq.JooqTest
import org.springframework.context.annotation.Import
import spock.lang.Specification
import uk.co.davidcryer.multitesting.generated.tables.pojos.Simple

@JooqTest
@Import([SimpleRepository, SimpleDbOps])
class SimpleRepositorySpec extends Specification {
    @Autowired
    private SimpleDbOps dbOps
    @Autowired
    private SimpleRepository repository

    def "add simple returns with generated id"() {
        when:
        def simple = repository.add new Simple(null, "test-name")

        then:
        verifyAll(simple) {
            id != null
            name == "test-name"
        }

        cleanup:
        dbOps.delete simple.id
    }

    def "get for entity that doesn't exist returns empty"() {
        expect:
        repository.get(1) == Optional.empty()
    }

    def "get returns matching entity"() {
        given:
        def simple = dbOps.insert new Simple(null, "test-name")

        expect:
        verifyAll(repository.get(simple.id).get()) {
            id == simple.id
            name == "test-name"
        }

        cleanup:
        dbOps.delete simple.id
    }
}
