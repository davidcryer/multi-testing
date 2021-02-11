package uk.co.davidcryer.multitesting.cucumber;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.co.davidcryer.multitesting.simple.SimpleRequest;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleSteps {

    @Autowired
    private TestRestTemplate restTemplate;
    private ResponseEntity<SimpleRequest> simpleResponse;

    @When("a new simple is posted")
    public void postNewSimple() {
        simpleResponse = restTemplate.postForEntity("/simple", new SimpleRequest(null, "test-name"), SimpleRequest.class);
    }

    @Then("a new simple is created")
    public void newSimpleIsCreated() {
        assertThat(simpleResponse).isNotNull();
        assertThat(simpleResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        SimpleRequest body = simpleResponse.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getId()).isNotNull();
        assertThat(body.getName()).isEqualTo("test-name");
    }
}
