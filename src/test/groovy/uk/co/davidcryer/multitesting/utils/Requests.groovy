package uk.co.davidcryer.multitesting.utils

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders

import static org.springframework.http.MediaType.APPLICATION_JSON

class Requests {

    static def post(Object content) {
        def headers = new HttpHeaders()
        headers.setContentType APPLICATION_JSON
        return new HttpEntity<>(content ,headers)
    }
}
