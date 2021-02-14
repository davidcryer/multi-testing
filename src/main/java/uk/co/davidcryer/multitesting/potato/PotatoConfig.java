package uk.co.davidcryer.multitesting.potato;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class PotatoConfig {

    @Bean
    public RestTemplate potatoTemplate() {
        return new RestTemplateBuilder()
                .rootUri("http://localhost:9876")
                .build();
    }
}
