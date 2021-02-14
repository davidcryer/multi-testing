package uk.co.davidcryer.multitesting.cucumber;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import uk.co.davidcryer.multitesting.letter.LetterMessage;
import uk.co.davidcryer.multitesting.utils.KafkaHelper;

import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class OtherSteps {

    @Autowired
    private KafkaHelper kafkaHelper;

    private WireMockServer wireMockServer;

    @Bean
    public KafkaHelper kafkaHelper(@Value("@{kafka.server}") String kafkaServer) {
        return new KafkaHelper(kafkaServer).startConsumingAvroTopics(LetterMessage.TOPIC);
    }

    @Given("setup")
    public void beforeScenario() {
        wireMockServer = new WireMockServer(wireMockConfig().port(10864));
        wireMockServer.start();
    }

    @After
    public void afterScenario(){
        if (Objects.nonNull(wireMockServer)) {
            wireMockServer.stop();
        }
        kafkaHelper.clearAllConsumedMessages();
    }

    @Given("Search One does not have any post code info")
    public void searchOneDoesNotHaveAnyPostCodeInfo() {
        wireMockServer.stubFor(get(urlMatching("/api/v1/postcode.*"))
                .willReturn(aResponse()
                        .withStatus(404)
                ));
    }

//    @Then("{int} messages were sent to onwards to search one")
//    public void messagesWereSentToOnwardsToSearchOne(int numberOfMessages) throws InterruptedException {
//        List<PostcodeAddress> postcodeAddressList = kafkaHelper.getAndClearConsumedMessages("test-topic", PostcodeAddress.class, numberOfMessages, 10000);
//
//        assertThat(postcodeAddressList).hasSize(numberOfMessages);
//    }
}
