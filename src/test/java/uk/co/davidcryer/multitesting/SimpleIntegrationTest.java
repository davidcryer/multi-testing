package uk.co.davidcryer.multitesting;

import org.jooq.DSLContext;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.davidcryer.multitesting.generated.tables.pojos.Simple;
import uk.co.davidcryer.multitesting.generated.tables.records.SimpleRecord;
import uk.co.davidcryer.multitesting.simple.SimpleRequest;
import uk.co.davidcryer.multitesting.utils.Requests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jooq.impl.DSL.trueCondition;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static uk.co.davidcryer.multitesting.generated.tables.Simple.SIMPLE;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SimpleIntegrationTest {
    @Autowired
    private TestRestTemplate template;
    @Autowired
    private DSLContext dslContext;

    @After
    public void afterTest() {
        dslContext.deleteFrom(SIMPLE).where(trueCondition());
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
        assertThat(simpleResponse).isEqualTo(SimpleRequest.builder()
                .id(simpleResponse.getId())
                .name("test-name-post")
                .build()
        );

        var entity = getEntity(simpleResponse.getId());
        assertThat(simpleResponse.getId()).isEqualTo(entity.getId());
        assertThat(simpleResponse.getName()).isEqualTo(entity.getName());
    }

    @Test
    public void getOne() {
        var simple = insertEntity(new Simple(null, "test-name-get"));

        var response = template.getForEntity("/simple/" + simple.getId(), SimpleRequest.class);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        var simpleResponse = response.getBody();
        assertThat(simpleResponse).isNotNull();
        assertThat(simpleResponse).isEqualTo(SimpleRequest.builder()
                .id(simple.getId())
                .name("test-name-get")
                .build()
        );
    }

    private Simple getEntity(Integer id) {
        return dslContext
                .selectFrom(SIMPLE)
                .where(SIMPLE.ID.eq(id))
                .fetchOne()
                .into(Simple.class);
    }

    private Simple insertEntity(Simple simple) {
        var record = dslContext.newRecord(SIMPLE, simple);
        record.store();
        return record.into(Simple.class);
    }

    private void deleteEntity(Integer id) {
        dslContext
                .deleteFrom(SIMPLE)
                .where(SIMPLE.ID.eq(id))
                .execute();
    }
}
