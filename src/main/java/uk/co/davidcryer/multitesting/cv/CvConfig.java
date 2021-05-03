package uk.co.davidcryer.multitesting.cv;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CvConfig {

    @Bean
    public RestTemplate cvTemplate() {
        return new RestTemplateBuilder()
                .rootUri("http://localhost:9875")
                .build();
    }
}
