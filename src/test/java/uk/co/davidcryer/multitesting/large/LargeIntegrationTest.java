package uk.co.davidcryer.multitesting.large;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.co.davidcryer.multitesting.generated.tables.pojos.Large;
import uk.co.davidcryer.multitesting.utils.Requests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(LargeDbOps.class)
public class LargeIntegrationTest {
    @Autowired
    private TestRestTemplate template;
    @Autowired
    private LargeDbOps dbOps;

    @AfterEach
    public void afterEach() {
        dbOps.deleteAll();
    }

    @Test
    public void post() {
        var request = LargeRequest.builder()
                .first("1")
                .second("2")
                .third("3")
                .fourth("4")
                .fifth("5")
                .sixth("6")
                .seventh("7")
                .eighth("8")
                .ninth("9")
                .tenth("10")
                .eleventh("11")
                .twelfth("12")
                .thirteenth("13")
                .fourteenth("14")
                .fifteenth("15")
                .sixteenth("16")
                .seventeenth("17")
                .eighteenth("18")
                .nineteenth("19")
                .twentieth("20")
                .build();
        var response = template.postForEntity("/large", Requests.post(request), LargeRequest.class);

        assertThat(response.getStatusCode()).isEqualTo(CREATED);
        var largeResponse = response.getBody();
        assertThat(largeResponse).isNotNull();

        var large = dbOps.get(largeResponse.getId());
        assertThat(large.getId()).isNotNull();
        assertThat(large.getFirst()).isEqualTo(request.getFirst());
        assertThat(large.getSecond()).isEqualTo(request.getSecond());
        assertThat(large.getThird()).isEqualTo(request.getThird());
        assertThat(large.getFourth()).isEqualTo(request.getFourth());
        assertThat(large.getFifth()).isEqualTo(request.getFifth());
        assertThat(large.getSixth()).isEqualTo(request.getSixth());
        assertThat(large.getSeventh()).isEqualTo(request.getSeventh());
        assertThat(large.getEighth()).isEqualTo(request.getEighth());
        assertThat(large.getNinth()).isEqualTo(request.getNinth());
        assertThat(large.getTenth()).isEqualTo(request.getTenth());
        assertThat(large.getEleventh()).isEqualTo(request.getEleventh());
        assertThat(large.getTwelfth()).isEqualTo(request.getTwelfth());
        assertThat(large.getThirteenth()).isEqualTo(request.getThirteenth());
        assertThat(large.getFourteenth()).isEqualTo(request.getFourteenth());
        assertThat(large.getFifteenth()).isEqualTo(request.getFifteenth());
        assertThat(large.getSixteenth()).isEqualTo(request.getSixteenth());
        assertThat(large.getSeventeenth()).isEqualTo(request.getSeventeenth());
        assertThat(large.getEighteenth()).isEqualTo(request.getEighteenth());
        assertThat(large.getNineteenth()).isEqualTo(request.getNineteenth());
        assertThat(large.getTwentieth()).isEqualTo(request.getTwentieth());

        assertThat(largeResponse).isEqualTo(LargeRequest.builder()
                .id(large.getId())
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

    @Test
    public void get() {
        var large = dbOps.add(new Large(
                "ID",
                "1",
                "2",
                "3",
                "4",
                "5",
                "6",
                "7",
                "8",
                "9",
                "10",
                "11",
                "12",
                "13",
                "14",
                "15",
                "16",
                "17",
                "18",
                "19",
                "20"
        ));

        var response = template.getForEntity("/large/" + large.getId(), LargeRequest.class);

        assertThat(response.getStatusCode()).isEqualTo(OK);
        var largeResponse = response.getBody();
        assertThat(largeResponse).isNotNull();
        assertThat(largeResponse).isEqualTo(LargeRequest.builder()
                .id(large.getId())
                .first(large.getFirst())
                .second(large.getSecond())
                .third(large.getThird())
                .fourth(large.getFourth())
                .fifth(large.getFifth())
                .sixth(large.getSixth())
                .seventh(large.getSeventh())
                .eighth(large.getEighth())
                .ninth(large.getNinth())
                .tenth(large.getTenth())
                .eleventh(large.getEleventh())
                .twelfth(large.getTwelfth())
                .thirteenth(large.getThirteenth())
                .fourteenth(large.getFourteenth())
                .fifteenth(large.getFifteenth())
                .sixteenth(large.getSixteenth())
                .seventeenth(large.getSeventeenth())
                .eighteenth(large.getEighteenth())
                .nineteenth(large.getNineteenth())
                .twentieth(large.getTwentieth())
                .build()
        );
    }
}
