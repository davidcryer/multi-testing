package uk.co.davidcryer.multitesting.simple

import spock.lang.Specification
import uk.co.davidcryer.multitesting.generated.tables.pojos.Simple

class SimpleServiceSpec extends Specification {
    private SimpleRepository repository = Stub()
    private SimpleService service = new SimpleService(repository)

    def "create simple"() {
        given:
        repository.add(_ as Simple) >> { Simple simple -> simple.setId(1); simple }

        when:
        def response = service.create new SimpleRequest(null, "test-name")

        then:
        verifyAll(response) {
            id == 1
            name == "test-name"
        }
    }

    def "get sample"() {
        given:
        repository.get(1) >> new Simple(1, "test-name")

        when:
        def response = service.get 1

        then:
        verifyAll(response) {
            id == 1
            name == "test-name"
        }
    }
}
