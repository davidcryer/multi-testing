package uk.co.davidcryer.multitesting.cv

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Bean
import spock.lang.Specification
import uk.co.davidcryer.multitesting.utils.Requests
import uk.co.davidcryer.multitesting.utils.TestKafkaConsumer

import java.time.LocalDateTime

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import static groovy.json.JsonOutput.prettyPrint
import static org.springframework.http.HttpStatus.ACCEPTED
import static org.springframework.http.HttpStatus.CONFLICT

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CvIntegrationSpec extends Specification {
    @Autowired
    private TestRestTemplate template
    @Autowired
    private TestKafkaConsumer<CvMessage> kafkaHelper
    @Autowired
    private CvDbOps dbOps
    @Autowired
    private ObjectMapper objectMapper
    private WireMockServer cvClient = new WireMockServer(wireMockConfig().port(9875))

    @TestConfiguration
    static class Config {
        @Bean
        TestKafkaConsumer<CvMessage> kafkaHelper(@Value("\${kafka.server}") String kafkaServer) {
            TestKafkaConsumer.builder()
                    .kafkaServer(kafkaServer)
                    .groupId("cv-spec")
                    .topic(CvMessage.TOPIC)
                    .topicClass(CvMessage)
                    .build()
        }
    }

    def setup() {
        kafkaHelper.clear()
    }

    def "posting cv should be stored in database, published to kafka and external client, and updated with publish status"() {
        given:
        def testStart = LocalDateTime.now()
        def request = objectMapper.readValue"""
{
    "name": "test-name",
    "emailAddress": "test-email",
    "phoneNumber": "test-number",
    "content": "test-content"
}
""", CvRequest

        and:
        cvClient.start()
        cvClient.stubFor(post("/cvs").willReturn(aResponse().withStatus(201)))

        when:
        def response = template.postForEntity"/cvs", Requests.post(request), String

        then: "assert response"
        response.statusCode == ACCEPTED

        and: "assert database entry"
        def kafkaMessage = kafkaHelper.get(1, 2000).get(0)
        Thread.sleep 500
        def cv = dbOps.get kafkaMessage.id

        verifyAll(cv) {
            id == UUID.fromString(id).toString()
            created.isAfter(testStart) && created.isBefore(LocalDateTime.now())
            name == request.name
            emailAddress == request.emailAddress
            phoneNumber == request.phoneNumber
            content == request.content
            isPublishedToClient
            isPublishedToKafka
            isFullyPublished
        }

        and: "assert kafka message"
        prettyPrint(json(kafkaMessage)) == """
{
    "id": "$cv.id",
    "created": ${json(cv.created)},
    "name": "$request.name",
    "emailAddress": "$request.emailAddress",
    "phoneNumber": "$request.phoneNumber",
    "content": "$request.content"
}
""".trim()

        and: "assert client request"
        cvClient.verify(postRequestedFor(urlPathEqualTo("/cvs"))
                .withRequestBody(equalToJson("""
{
    "id": "$cv.id",
    "created": ${json(cv.created)},
    "name": "$request.name",
    "emailAddress": "$request.emailAddress",
    "phoneNumber": "$request.phoneNumber",
    "content": "$request.content"
}
""")))

        cleanup:
        kafkaHelper.clear()
        dbOps.deleteAll()
        cvClient.stop()
    }

    def "More than one CV with same email address cannot be processed at same time"() {
        given:
        def request = objectMapper.readValue"""
{
    "emailAddress": "test-email-2"
}
""", CvRequest

        when:
        def firstResponse = template.postForEntity"/cvs", Requests.post(request), String
        def secondResponse = template.postForEntity"/cvs", Requests.post(request), String

        then:
        verifyAll {
            firstResponse.statusCode == ACCEPTED
            secondResponse.statusCode == CONFLICT
        }

        cleanup:
        kafkaHelper.get 1, 2000
        Thread.sleep 500
        dbOps.deleteAll()
    }

    private def json(Object o) {//json friendly
        objectMapper.writeValueAsString o
    }
}
