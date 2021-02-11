package uk.co.davidcryer.multitesting

import org.jooq.DSLContext
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

import static uk.co.davidcryer.multitesting.generated.tables.Simple.SIMPLE

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SimpleIntegrationSpec extends Specification {

    @Autowired
    private TestRestTemplate template
    @Autowired
    private DSLContext dslContext

    def "posting simple should save in database"() {
        when:
        def response = template.postForEntity "/simple", Requests.post("""
{
    "name": "test-name-post"
}
"""), SimpleRequest

        then: "assert entity"
        def simple = getEntity(response.body.id)
        verifyAll(simple) {
            id != null
            name == "test-name-post"
        }

        and: "assert response"
        response.statusCode == HttpStatus.CREATED
        prettyPrint(toJson(response.body)) == """
{
    "id": ${simple.id},
    "name": ${simple.name}
}
""".trim()


        cleanup:
        deleteEntity(simple.id)
    }

    def "get simple"() {
        given:
        def simple = insertEntity new Simple(null, "test-name-get")

        when:
        def response = template.getForEntity"/simple/${simple.id}", SimpleRequest

        then:
        response.statusCode == HttpStatus.OK
        prettyPrint(toJson(response.body)) == """
{
    "id": ${simple.id},
    "name": ${simple.name}
}
""".trim()

        cleanup:
        deleteEntity(simple.id)
    }

    private def getEntity(Integer id) {
        dslContext
                .selectFrom(SIMPLE)
                .where(SIMPLE.ID.eq(id))
                .fetchOne()
                .into(Simple)
    }

    private def insertEntity(Simple simple) {
        def record = dslContext.newRecord(SIMPLE, simple)
        record.store()
        record.into(Simple)
    }

    private def deleteEntity(Integer id) {
        dslContext
                .deleteFrom(SIMPLE)
                .where(SIMPLE.ID.eq(id))
                .execute()
    }
}
