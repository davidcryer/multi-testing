package uk.co.davidcryer.multitesting.labour;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.ZonedDateTime;

@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@Builder
public class FruitMessage {
    public static final String TOPIC = "fruit";
    private String id;
    private ZonedDateTime created;
    private String description;
}
