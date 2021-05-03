package uk.co.davidcryer.multitesting.cv;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CvClient {
    private final RestTemplate cvTemplate;
    private final ObjectMapper objectMapper;

    public boolean post(CvClientRequest request) {
        var response = cvTemplate.exchange("/cvs", POST, toRequestEntity(request), String.class);
        var responseCode = response.getStatusCode();
        if (responseCode.is2xxSuccessful()) {
            return true;
        }
        var responseBody = Optional.ofNullable(response.getBody()).orElse("");
        log.error("cv service returned code {} with error {} for request {}", responseCode.value(), responseBody, request);
        return false;
    }

    private HttpEntity<?> toRequestEntity(Object request) {
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        try {
            return new HttpEntity<>(objectMapper.writeValueAsString(request), headers);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@Builder
class CvClientRequest {
    private String id;
    private LocalDateTime created;
    private String name;
    private String emailAddress;
    private String phoneNumber;
    private String content;
}
