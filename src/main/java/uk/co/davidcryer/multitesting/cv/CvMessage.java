package uk.co.davidcryer.multitesting.cv;

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
public class CvMessage {
    public static final String TOPIC = "cv";
    private String id;
    private ZonedDateTime created;
    private String name;
    private String emailAddress;
    private String phoneNumber;
    private String content;
}
