package uk.co.davidcryer.multitesting.potato

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import spock.lang.Specification

import static com.github.tomakehurst.wiremock.client.WireMock.*
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PotatoIntegrationSpec extends Specification {
    @Autowired
    private TestRestTemplate template
    @Autowired
    private Producer<String, Object> kafkaProducer
    private WireMockServer potatoClient
    @Autowired
    private ObjectMapper objectMapper

    def "consuming potato passes it on to external service"() {
        given:
        def potato = objectMapper.readValue"""
{
    "id": "potato-id",
    "type": "a potato",
    "description": "straight out of the oven!",
    "temperature": {
        "value": "30000",
        "unit": "K"
    }
}
""", PotatoMessage

        and:
        potatoClient = new WireMockServer(wireMockConfig().port(9876))
        potatoClient.start()
        potatoClient.stubFor(put("/potato/${potato.id}").willReturn(aResponse().withStatus(200)))

        when:
        kafkaProducer.send new ProducerRecord<String, Object>(PotatoMessage.TOPIC, potato.id, potato)
        Thread.sleep 1000

        then:
        potatoClient.verify(putRequestedFor(urlPathEqualTo("/potato/${potato.id}"))
                .withRequestBody(equalToJson("""
{
    "id": "${potato.id}",
    "type": "${potato.type}",
    "description": "${potato.description}",
    "temperature": "${potato.temperature.value}${potato.temperature.unit}"
}
""".trim())))

        cleanup:
        potatoClient.stop()
    }
}