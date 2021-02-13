package uk.co.davidcryer.multitesting.large

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import spock.lang.Specification
import uk.co.davidcryer.multitesting.generated.tables.pojos.Large
import uk.co.davidcryer.multitesting.utils.Requests

import static groovy.json.JsonOutput.prettyPrint
import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.OK

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LargeIntegrationSpec extends Specification {
    @Autowired
    private TestRestTemplate template
    @Autowired
    private LargeDbOps dbOps
    @Autowired
    private ObjectMapper objectMapper

    def "posting large saves in database"() {
        given:
        def request = objectMapper.readValue"""
{
    "first": "1",
    "second": "2",
    "third": "3",
    "fourth": "4",
    "fifth": "5",
    "sixth": "6",
    "seventh": "7",
    "eighth": "8",
    "ninth": "9",
    "tenth": "10",
    "eleventh": "11",
    "twelfth": "12",
    "thirteenth": "13",
    "fourteenth": "14",
    "fifteenth": "15",
    "sixteenth": "16",
    "seventeenth": "17",
    "eighteenth": "18",
    "nineteenth": "19",
    "twentieth": "20"
}
""", LargeRequest

        when:
        def response = template.postForEntity"/large", Requests.post(request), String

        then: "assert entity"
        def id = objectMapper.readValue(response.body, LargeRequest).id
        def large = dbOps.get id
        verifyAll(large) {
            it.id == id
            first == request.first
            second == request.second
            third == request.third
            fourth == request.fourth
            fifth == request.fifth
            sixth == request.sixth
            seventh == request.seventh
            eighth == request.eighth
            ninth == request.ninth
            tenth == request.tenth
            eleventh == request.eleventh
            twelfth == request.twelfth
            thirteenth == request.thirteenth
            fourteenth == request.fourteenth
            fifteenth == request.fifteenth
            sixteenth == request.sixteenth
            seventeenth == request.seventeenth
            eighteenth == request.eighteenth
            nineteenth == request.nineteenth
            twentieth == request.twentieth
        }

        and: "assert response"
        response.statusCode == CREATED
        prettyPrint(response.body) == """
{
    "id": "${large.id}",
    "first": "${request.first}",
    "second": "${request.second}",
    "third": "${request.third}",
    "fourth": "${request.fourth}",
    "fifth": "${request.fifth}",
    "sixth": "${request.sixth}",
    "seventh": "${request.seventh}",
    "eighth": "${request.eighth}",
    "ninth": "${request.ninth}",
    "tenth": "${request.tenth}",
    "eleventh": "${request.eleventh}",
    "twelfth": "${request.twelfth}",
    "thirteenth": "${request.thirteenth}",
    "fourteenth": "${request.fourteenth}",
    "fifteenth": "${request.fifteenth}",
    "sixteenth": "${request.sixteenth}",
    "seventeenth": "${request.seventeenth}",
    "eighteenth": "${request.eighteenth}",
    "nineteenth": "${request.nineteenth}",
    "twentieth": "${request.twentieth}"
}
""".trim()

        cleanup:
        dbOps.delete large.id
    }

    def "getting large"() {
        given:
        def large = dbOps.add new Large(
                id: UUID.randomUUID().toString(),
                first: "1",
                second: "2",
                third: "3",
                fourth: "4",
                fifth: "5",
                sixth: "6",
                seventh: "7",
                eighth: "8",
                ninth: "9",
                tenth: "10",
                eleventh: "11",
                twelfth: "12",
                thirteenth: "13",
                fourteenth: "14",
                fifteenth: "15",
                sixteenth: "16",
                seventeenth: "17",
                eighteenth: "18",
                nineteenth: "19",
                twentieth: "20"
        )

        when:
        def response = template.getForEntity"/large/${large.id}", String

        then:
        response.statusCode == OK
        prettyPrint(response.body) == """
{
    "id": "${large.id}",
    "first": "${large.first}",
    "second": "${large.second}",
    "third": "${large.third}",
    "fourth": "${large.fourth}",
    "fifth": "${large.fifth}",
    "sixth": "${large.sixth}",
    "seventh": "${large.seventh}",
    "eighth": "${large.eighth}",
    "ninth": "${large.ninth}",
    "tenth": "${large.tenth}",
    "eleventh": "${large.eleventh}",
    "twelfth": "${large.twelfth}",
    "thirteenth": "${large.thirteenth}",
    "fourteenth": "${large.fourteenth}",
    "fifteenth": "${large.fifteenth}",
    "sixteenth": "${large.sixteenth}",
    "seventeenth": "${large.seventeenth}",
    "eighteenth": "${large.eighteenth}",
    "nineteenth": "${large.nineteenth}",
    "twentieth": "${large.twentieth}"
}
""".trim()

        cleanup:
        dbOps.delete large.id
    }
}
