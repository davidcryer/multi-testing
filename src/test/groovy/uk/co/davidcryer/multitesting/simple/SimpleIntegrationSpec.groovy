package uk.co.davidcryer.multitesting.simple

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import spock.lang.Specification
import uk.co.davidcryer.multitesting.generated.tables.pojos.Simple
import uk.co.davidcryer.multitesting.utils.Requests

import static groovy.json.JsonOutput.prettyPrint
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.OK

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SimpleIntegrationSpec extends Specification {
    @Autowired
    private TestRestTemplate template
    @Autowired
    private SimpleDbOps dbOps
    @Autowired
    private ObjectMapper objectMapper

    def "posting simple saves in database"() {
        given:
        def request = objectMapper.readValue"""
{
    "name": "test-name-post"
}
""", SimpleRequest

        when:
        def response = template.postForEntity"/simple", Requests.post(request), String

        then: "assert entity"
        def id = objectMapper.readValue(response.body, SimpleRequest).id
        def simple = dbOps.get(id)
        verifyAll(simple) {
            it.id == id
            name == request.name
        }

        and: "assert response"
        response.statusCode == HttpStatus.CREATED
        prettyPrint(response.body) == """
{
    "id": ${simple.id},
    "name": "${request.name}"
}
""".trim()

        cleanup:
        dbOps.delete simple.id
    }

    def "getting simple"() {
        given:
        def simple = dbOps.add new Simple(null, "test-name-get")

        when:
        def response = template.getForEntity"/simple/${simple.id}", String

        then:
        response.statusCode == OK
        prettyPrint(response.body) == """
{
    "id": ${simple.id},
    "name": "${simple.name}"
}
""".trim()

        cleanup:
        dbOps.delete simple.id
    }

    def "getting non-existing simple returns 404"() {
        when:
        def response = template.getForEntity"/simple/1", String

        then:
        response.statusCode == NOT_FOUND
    }
}
