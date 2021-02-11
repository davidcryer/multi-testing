package uk.co.davidcryer.multitesting.simple;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@AllArgsConstructor
@Data
public class SimpleRequest {
    private Integer id;
    private String name;
}
