package uk.co.davidcryer.multitesting.letter;

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
public class LetterMessage {
    public static final String TOPIC = "complex-topic";
    private String id;
    private String sender;
    private String recipient;
    private Address recipientAddress;
    private String message;


    @Jacksonized
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @AllArgsConstructor
    @Builder
    public static class Address {
        private String buildingNumber;
        private String organisation;
        private String addressLine1;
        private String addressLine2;
        private String county;
        private String town;
        private String postcode;
    }
}
