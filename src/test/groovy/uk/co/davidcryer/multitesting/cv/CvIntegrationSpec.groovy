package uk.co.davidcryer.multitesting.cv

import com.fasterxml.jackson.databind.ObjectMapper
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

import static groovy.json.JsonOutput.prettyPrint
import static org.springframework.http.HttpStatus.ACCEPTED

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CvIntegrationSpec extends Specification {
    @Autowired
    private TestRestTemplate template
    @Autowired
    private TestKafkaConsumer<CvMessage> kafkaHelper
    @Autowired
    private ObjectMapper objectMapper

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

    def "posting cv should be stored in database and published to kafka"() {
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
        when:
        def response = template.postForEntity"/cv", Requests.post(request), String

        then: "assert response"
        response.statusCode == ACCEPTED

        and: "assert queued message generated properties"
        def message = kafkaHelper.get(1, 2000).get(0)
        verifyAll(message) {
            id == UUID.fromString(id).toString()
            created.isAfter(testStart) && created.isBefore(LocalDateTime.now())
            name == request.name
            emailAddress == request.emailAddress
            phoneNumber == request.phoneNumber
            content == request.content
        }

        and: "assert queued message json"
        prettyPrint(json(message)) == """
{
    "id": "$message.id",
    "created": ${json(message.created)},
    "name": "$request.name",
    "emailAddress": "$request.emailAddress",
    "phoneNumber": "$request.phoneNumber",
    "content": "$request.content"
}
""".trim()

        cleanup:
        kafkaHelper.clear()
    }

    private def json(Object o) {//json friendly
        objectMapper.writeValueAsString o
    }
}
