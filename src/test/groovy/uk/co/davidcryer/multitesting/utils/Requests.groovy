package uk.co.davidcryer.multitesting.utils

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders

import static org.springframework.http.MediaType.APPLICATION_JSON

class Requests {

    static def post(String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType APPLICATION_JSON;
        return new HttpEntity<String>(content ,headers);
    }
}
