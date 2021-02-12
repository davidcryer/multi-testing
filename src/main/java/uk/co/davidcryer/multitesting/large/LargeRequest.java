package uk.co.davidcryer.multitesting.large;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@Data
public class LargeRequest {
    private String id;
    private String first;
    private String second;
    private String third;
    private String fourth;
    private String fifth;
    private String sixth;
    private String seventh;
    private String eighth;
    private String ninth;
    private String tenth;
    private String eleventh;
    private String twelfth;
    private String thirteenth;
    private String fourteenth;
    private String fifteenth;
    private String sixteenth;
    private String seventeenth;
    private String eighteenth;
    private String nineteenth;
    private String twentieth;
}
