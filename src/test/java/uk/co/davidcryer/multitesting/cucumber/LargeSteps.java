package uk.co.davidcryer.multitesting.cucumber;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import uk.co.davidcryer.multitesting.generated.tables.pojos.Large;
import uk.co.davidcryer.multitesting.large.LargeDbOps;
import uk.co.davidcryer.multitesting.large.LargeRequest;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@Import(LargeDbOps.class)
public class LargeSteps {

    @Autowired
    private TestRestTemplate template;
    @Autowired
    private LargeDbOps dbOps;
    private LargeRequest request;
    private ResponseEntity<LargeRequest> response;
    private Large entity;

    @When("a new large is posted")
    public void postNewLarge() {
        request = LargeData.request();
        response = template.postForEntity("/large", request, LargeRequest.class);
    }

    @Then("a new large is created matching request")
    public void newLargeIsCreatedMatchingRequest() {
        Objects.requireNonNull(request);
        var id = Objects.requireNonNull(response.getBody()).getId();
        var entity = dbOps.get(id);
        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getId()).isNotNull();
        assertThat(entity.getFirst()).isEqualTo(request.getFirst());
        assertThat(entity.getSecond()).isEqualTo(request.getSecond());
        assertThat(entity.getThird()).isEqualTo(request.getThird());
        assertThat(entity.getFourth()).isEqualTo(request.getFourth());
        assertThat(entity.getFifth()).isEqualTo(request.getFifth());
        assertThat(entity.getSixth()).isEqualTo(request.getSixth());
        assertThat(entity.getSeventh()).isEqualTo(request.getSeventh());
        assertThat(entity.getEighth()).isEqualTo(request.getEighth());
        assertThat(entity.getNinth()).isEqualTo(request.getNinth());
        assertThat(entity.getTenth()).isEqualTo(request.getTenth());
        assertThat(entity.getEleventh()).isEqualTo(request.getEleventh());
        assertThat(entity.getTwelfth()).isEqualTo(request.getTwelfth());
        assertThat(entity.getThirteenth()).isEqualTo(request.getThirteenth());
        assertThat(entity.getFourteenth()).isEqualTo(request.getFourteenth());
        assertThat(entity.getFifteenth()).isEqualTo(request.getFifteenth());
        assertThat(entity.getSixteenth()).isEqualTo(request.getSixteenth());
        assertThat(entity.getSeventeenth()).isEqualTo(request.getSeventeenth());
        assertThat(entity.getEighteenth()).isEqualTo(request.getEighteenth());
        assertThat(entity.getNineteenth()).isEqualTo(request.getNineteenth());
        assertThat(entity.getTwentieth()).isEqualTo(request.getTwentieth());
    }

    @Then("the large post response matches the request with generated id")
    public void postMatchesRequest() {
        var body = Objects.requireNonNull(response).getBody();
        assertThat(body).isNotNull();
        assertThat(body).isEqualTo(LargeRequest.builder()
                .id(dbOps.get(body.getId()).getId())
                .first(request.getFirst())
                .second(request.getSecond())
                .third(request.getThird())
                .fourth(request.getFourth())
                .fifth(request.getFifth())
                .sixth(request.getSixth())
                .seventh(request.getSeventh())
                .eighth(request.getEighth())
                .ninth(request.getNinth())
                .tenth(request.getTenth())
                .eleventh(request.getEleventh())
                .twelfth(request.getTwelfth())
                .thirteenth(request.getThirteenth())
                .fourteenth(request.getFourteenth())
                .fifteenth(request.getFifteenth())
                .sixteenth(request.getSixteenth())
                .seventeenth(request.getSeventeenth())
                .eighteenth(request.getEighteenth())
                .nineteenth(request.getNineteenth())
                .twentieth(request.getTwentieth())
                .build()
        );
    }

    @Given("a large exists")
    public void aLargeExists() {
        entity = dbOps.add(LargeData.entity());
    }

    @When("the large is fetched")
    public void theLargeIsFetched() {
        Objects.requireNonNull(entity);
        response = template.getForEntity("/large/" + entity.getId(), LargeRequest.class);
    }

    @Then("the response matches the large")
    public void theResponseMatchesTheLarge() {
        Objects.requireNonNull(entity);
        var body = Objects.requireNonNull(response).getBody();
        assertThat(body).isNotNull();
        assertThat(body).isEqualTo(LargeRequest.builder()
                .id(entity.getId())
                .first(entity.getFirst())
                .second(entity.getSecond())
                .third(entity.getThird())
                .fourth(entity.getFourth())
                .fifth(entity.getFifth())
                .sixth(entity.getSixth())
                .seventh(entity.getSeventh())
                .eighth(entity.getEighth())
                .ninth(entity.getNinth())
                .tenth(entity.getTenth())
                .eleventh(entity.getEleventh())
                .twelfth(entity.getTwelfth())
                .thirteenth(entity.getThirteenth())
                .fourteenth(entity.getFourteenth())
                .fifteenth(entity.getFifteenth())
                .sixteenth(entity.getSixteenth())
                .seventeenth(entity.getSeventeenth())
                .eighteenth(entity.getEighteenth())
                .nineteenth(entity.getNineteenth())
                .twentieth(entity.getTwentieth())
                .build()
        );
    }
}