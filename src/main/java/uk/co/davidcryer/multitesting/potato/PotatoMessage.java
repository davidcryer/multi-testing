package uk.co.davidcryer.multitesting.potato;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@Builder
public class PotatoMessage {
    public static final String TOPIC = "potato";
    private String id;
    private String type;
    private String description;
    private Temperature temperature;

    @Jacksonized
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @AllArgsConstructor
    @Builder
    public static class Temperature {
        private String value;
        private String unit;
    }
}
