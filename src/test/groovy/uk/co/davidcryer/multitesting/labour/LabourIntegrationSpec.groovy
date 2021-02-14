package uk.co.davidcryer.multitesting.labour

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

import java.time.Instant

import static org.springframework.http.HttpStatus.OK

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LabourIntegrationSpec extends Specification {
    @Autowired
    private TestRestTemplate template
    @Autowired
    private TestKafkaConsumer<FruitMessage> kafkaHelper
    @Autowired
    private ObjectMapper objectMapper

    @TestConfiguration
    static class Config {
        @Bean
        TestKafkaConsumer<FruitMessage> kafkaHelper(@Value("\${kafka.server}") String kafkaServer) {
            TestKafkaConsumer.builder()
                    .kafkaServer(kafkaServer)
                    .groupId("labour-spec")
                    .topic(FruitMessage.TOPIC)
                    .topicClass(FruitMessage)
                    .build()
        }
    }

    def "posting fruit puts it onto kafka queue"() {
        given:
        def testStart = Instant.now()
        def fruit = objectMapper.readValue"""
{
    "description": "shiny red apple"
}
""", FruitRequest
        when:
        def response = template.postForEntity"/labour", Requests.post(fruit), String

        then: "assert response"
        response.statusCode == OK

        and: "assert queued message"
        def messages = kafkaHelper.get 1, 2000
        messages.size() == 1
        verifyAll(messages.get(0)) {
            id != null
            created > testStart && created < Instant.now()
            description == fruit.description
        }

        cleanup:
        kafkaHelper.clear()
    }

    def "do the same again to see how TestKafkaConsumer behaves"() {
        given:
        def testStart = Instant.now()
        def fruit = objectMapper.readValue"""
{
    "description": "bright juicy orange"
}
""", FruitRequest
        when:
        def response = template.postForEntity"/labour", Requests.post(fruit), String

        then: "assert response"
        response.statusCode == OK

        and: "assert queued message"
        def messages = kafkaHelper.get 1, 2000
        messages.size() == 1
        verifyAll(messages.get(0)) {
            id != null
            created > testStart && created < Instant.now()
            description == fruit.description
        }

        cleanup:
        kafkaHelper.clear()
    }
}
