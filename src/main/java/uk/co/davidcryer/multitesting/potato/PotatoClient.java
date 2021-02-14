package uk.co.davidcryer.multitesting.potato;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PotatoClient {
    private final RestTemplate potatoTemplate;

    void put(PotatoClientRequest request) {
        var response = potatoTemplate.exchange("/potato/" + request.getId(), PUT, toRequestEntity(request), String.class);
        var responseCode = response.getStatusCode();
        if (responseCode.is4xxClientError() || responseCode.is5xxServerError()) {
            var responseBody = Optional.ofNullable(response.getBody()).orElse("");
            log.error("potato service returned code {} with error {} for request {}", responseCode.value(), responseBody, request);
        }
        if (responseCode.is4xxClientError()) {
            throw new RuntimeException("potato client returned error");
        }
    }

    private HttpEntity<?> toRequestEntity(PotatoClientRequest request) {
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        return new HttpEntity<>(request, headers);
    }
}
