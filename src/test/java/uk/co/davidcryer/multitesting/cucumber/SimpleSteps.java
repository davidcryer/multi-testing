package uk.co.davidcryer.multitesting.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import uk.co.davidcryer.multitesting.generated.tables.pojos.Simple;
import uk.co.davidcryer.multitesting.simple.SimpleDbOps;
import uk.co.davidcryer.multitesting.simple.SimpleRequest;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@Import(SimpleDbOps.class)
public class SimpleSteps {

    @Autowired
    private TestRestTemplate template;
    @Autowired
    private SimpleDbOps dbOps;
    private SimpleRequest request;
    private ResponseEntity<SimpleRequest> response;
    private Simple entity;

    @When("a new simple is posted")
    public void postNewSimple() {
        request = SimpleData.request();
        response = template.postForEntity("/simple", request, SimpleRequest.class);
    }

    @Then("a new simple is created matching request")
    public void newSimpleIsCreatedMatchingRequest() {
        Objects.requireNonNull(request);
        var id = Objects.requireNonNull(response.getBody()).getId();
        var entity = dbOps.get(id);
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getName()).isEqualTo(request.getName());
    }

    @Then("the simple post response matches the request with generated id")
    public void postMatchesRequest() {
        var body = Objects.requireNonNull(response).getBody();
        assertThat(body).isNotNull();
        assertThat(body.getId()).isEqualTo(dbOps.get(body.getId()).getId());
        assertThat(body.getName()).isEqualTo(request.getName());
    }

    @Given("a simple exists")
    public void aSimpleExists() {
        entity = dbOps.add(SimpleData.entity());
    }

    @When("the simple is fetched")
    public void theSimpleIsFetched() {
        Objects.requireNonNull(entity);
        response = template.getForEntity("/simple/" + entity.getId(), SimpleRequest.class);
    }

    @Then("the response matches the simple")
    public void theResponseMatchesTheSimple() {
        Objects.requireNonNull(entity);
        var body = Objects.requireNonNull(response).getBody();
        assertThat(body).isNotNull();
        assertThat(body.getId()).isEqualTo(entity.getId());
        assertThat(body.getName()).isEqualTo(entity.getName());
    }
}
