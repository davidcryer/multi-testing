package uk.co.davidcryer.multitesting.letter

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LetterIntegrationSpec extends Specification {
    @Autowired
    private Producer<String, Object> kafkaProducer
    @Autowired
    private LetterDbOps dbOps
    @Autowired
    private ObjectMapper objectMapper

    def "consumed message is saved in database"() {
        given:
        def message = objectMapper.readValue"""
{
    "id": "test-id",
    "sender": "test-sender",
    "recipient": "test-recipient",
    "recipientAddress": {
        "buildingNumber": "test-building-number",
        "organisation": "test-organisation",
        "addressLine1": "test-address-line-1",
        "addressLine2": "test-address-line-2",
        "county": "test-county",
        "town": "test-town",
        "postcode": "test-pc"
    },
    "message": "test-message"
}
""", LetterMessage

        when:
        kafkaProducer.send new ProducerRecord<String, Object>(LetterMessage.TOPIC, message.id, message)
        Thread.sleep 1000

        then:
        def letter = dbOps.getLetter message.id
        def recipientAddress = dbOps.getRecipientAddress letter.recipientAddress
        verifyAll(letter) {
            id == message.id
            sender == message.sender
            recipient == message.recipient
            it.recipientAddress == recipientAddress.id
            it.message == message.message
        }
        verifyAll(recipientAddress) {
            id != null
            buildingNumber == message.recipientAddress.buildingNumber
            organisation == message.recipientAddress.organisation
            addressline1 == message.recipientAddress.addressLine1
            addressline2 == message.recipientAddress.addressLine2
            town == message.recipientAddress.town
            postcode == message.recipientAddress.postcode
        }

        cleanup:
        dbOps.delete message.id
    }
}
