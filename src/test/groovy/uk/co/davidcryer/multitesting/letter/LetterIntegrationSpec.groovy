package uk.co.davidcryer.multitesting.letter

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import spock.lang.Specification
import uk.co.davidcryer.multitesting.generated.tables.pojos.Address
import uk.co.davidcryer.multitesting.generated.tables.pojos.Letter

import static groovy.json.JsonOutput.prettyPrint
import static org.springframework.http.HttpStatus.OK

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LetterIntegrationSpec extends Specification {
    @Autowired
    private TestRestTemplate template
    @Autowired
    private Producer<String, Object> kafkaProducer
    @Autowired
    private LetterDbOps dbOps
    @Autowired
    private ObjectMapper objectMapper

    def "consuming message saves in database"() {
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
        def recipientAddress = dbOps.getAddress letter.recipientAddress
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
            addressLine_1 == message.recipientAddress.addressLine1
            addressLine_2 == message.recipientAddress.addressLine2
            town == message.recipientAddress.town
            postcode == message.recipientAddress.postcode
        }

        cleanup:
        dbOps.delete message.id
    }

    def "getting letter"() {
        given:
        def address = dbOps.addAddress new Address(
                id: "address-id",
                buildingNumber: "test-building-number",
                organisation: "test-organisation",
                addressLine_1: "test-address-line-1",
                addressLine_2: "test-address-line-2",
                county: "test-county",
                town: "test-town",
                postcode: "test-pc"
        )
        def letter = dbOps.addLetter new Letter(
                id: "letter-id",
                sender: "test-sender",
                recipient: "test-recipient",
                recipientAddress: address.id,
                message: "test-message"
        )

        when:
        def response = template.getForEntity"/letter/${letter.id}", String

        then:
        response.statusCode == OK
        prettyPrint(response.body) == """
{
    "id": "${letter.id}",
    "sender": "${letter.sender}",
    "recipient": "${letter.recipient}",
    "recipientAddress": {
        "buildingNumber": "${address.buildingNumber}",
        "organisation": "${address.organisation}",
        "addressLine1": "${address.addressLine_1}",
        "addressLine2": "${address.addressLine_2}",
        "county": "${address.county}",
        "town": "${address.town}",
        "postcode": "${address.postcode}"
    },
    "message": "${letter.message}"
}
""".trim()

        cleanup:
        dbOps.delete letter.id
    }
}
