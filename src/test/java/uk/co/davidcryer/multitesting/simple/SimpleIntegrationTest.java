package uk.co.davidcryer.multitesting.simple;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.davidcryer.multitesting.generated.tables.pojos.Simple;
import uk.co.davidcryer.multitesting.utils.Requests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(SimpleDbOps.class)
public class SimpleIntegrationTest {
    @Autowired
    private TestRestTemplate template;
    @Autowired
    private SimpleDbOps dbOps;

    @AfterEach
    public void afterEach() {
        dbOps.deleteAll();
    }

    @Test
    public void post() {
        var request = SimpleRequest.builder()
                .name("test-name-post")
                .build();
        var response = template.postForEntity(
                "/simple", Requests.post(request), SimpleRequest.class);

        assertThat(response.getStatusCode()).isEqualTo(CREATED);
        var simpleResponse = response.getBody();
        assertThat(simpleResponse).isNotNull();

        var entity = dbOps.get(simpleResponse.getId());
        assertThat(entity.getId()).isNotNull();
        assertThat(entity.getName()).isEqualTo(request.getName());

        assertThat(simpleResponse).isEqualTo(SimpleRequest.builder()
                .id(entity.getId())
                .name(request.getName())
                .build()
        );
    }

    @Test
    public void get() {
        var entity = dbOps.add(new Simple(null, "test-name-get"));

        var response = template.getForEntity("/simple/" + entity.getId(), SimpleRequest.class);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        var simpleResponse = response.getBody();
        assertThat(simpleResponse).isNotNull();
        assertThat(simpleResponse).isEqualTo(SimpleRequest.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build()
        );
    }

    @Test
    public void get_noEntityFoundReturns404() {
        var response = template.getForEntity("/simple/1", SimpleRequest.class);

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
    }
}
