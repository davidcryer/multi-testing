package uk.co.davidcryer.multitesting.simple


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import spock.lang.Specification
import uk.co.davidcryer.multitesting.generated.tables.pojos.Simple
import uk.co.davidcryer.multitesting.simple.SimpleRequest
import uk.co.davidcryer.multitesting.utils.Requests

import static groovy.json.JsonOutput.prettyPrint
import static groovy.json.JsonOutput.toJson
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.OK

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SimpleIntegrationSpec extends Specification {

    @Autowired
    private TestRestTemplate template
    @Autowired
    private SimpleDbOps dbOps

    def "posting simple should save in database"() {
        when:
        def response = template.postForEntity "/simple", Requests.post("""
{
    "name": "test-name-post"
}
"""), SimpleRequest

        then: "assert entity"
        def simple = dbOps.getEntity(response.body.id)
        verifyAll(simple) {
            id != null
            name == "test-name-post"
        }

        and: "assert response"
        response.statusCode == HttpStatus.CREATED
        prettyPrint(toJson(response.body)) == """
{
    "id": ${simple.id},
    "name": "${simple.name}"
}
""".trim()

        cleanup:
        dbOps.deleteEntity(simple.id)
    }

    def "getting simple"() {
        given:
        def simple = dbOps.insertEntity new Simple(null, "test-name-get")

        when:
        def response = template.getForEntity"/simple/${simple.id}", SimpleRequest

        then:
        response.statusCode == OK
        prettyPrint(toJson(response.body)) == """
{
    "id": ${simple.id},
    "name": "${simple.name}"
}
""".trim()

        cleanup:
        dbOps.deleteEntity(simple.id)
    }

    def "getting non-existing simple returns 404"() {
        when:
        def response = template.getForEntity "/simple/1", SimpleRequest

        then:
        response.statusCode == NOT_FOUND
    }
}
