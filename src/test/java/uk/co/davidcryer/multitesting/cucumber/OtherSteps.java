package uk.co.davidcryer.multitesting.cucumber;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.davidcryer.multitesting.utils.KafkaHelper;

import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static wiremock.com.google.common.io.Resources.getResource;

public class OtherSteps {

    @Autowired
    private KafkaHelper kafkaHelper;

    private WireMockServer wireMockServer;

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
