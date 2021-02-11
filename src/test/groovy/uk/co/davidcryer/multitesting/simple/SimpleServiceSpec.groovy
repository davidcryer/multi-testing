package uk.co.davidcryer.multitesting.simple

import spock.lang.Specification
import uk.co.davidcryer.multitesting.generated.tables.pojos.Simple

class SimpleServiceSpec extends Specification {
    private SimpleRepository repository = Stub()
    private SimpleService service = new SimpleService(repository)

    def "add simple"() {
        given:
        repository.add(_ as Simple) >> { Simple simple -> simple.setId(1); simple }

        when:
        def response = service.add new SimpleRequest(null, "test-name")

        then:
        verifyAll(response) {
            id == 1
            name == "test-name"
        }
    }

    def "ignores ids in added simples"() {
        given:
        repository.add(new Simple()) >> new Simple()

        expect:
        service.add(new SimpleRequest(-1, null)) == new SimpleRequest(null, null)
    }

    def "get sample"() {
        given:
        repository.get(1) >> new Simple(1, "test-name")

        expect:
        verifyAll(service.get(1)) {
            id == 1
            name == "test-name"
        }
    }
}
