package uk.co.davidcryer.multitesting.simple

import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification
import spock.mock.DetachedMockFactory
import uk.co.davidcryer.multitesting.simple.SimpleController
import uk.co.davidcryer.multitesting.simple.SimpleRequest
import uk.co.davidcryer.multitesting.simple.SimpleService

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post

@WebMvcTest(controllers = [SimpleController])
class SimpleControllerHttpSpec extends Specification {

    @Autowired
    MockMvc mockMvc
    @Autowired
    SimpleService simpleService

    def "post mapping"() {
        given:
        simpleService.create(_ as SimpleRequest) >> { SimpleRequest request -> request.setId(1); request }

        when:
        def response = mockMvc.perform(
                post("/simple")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
{
    "name": "test-name-post"
}
""")
        ).andReturn().response

        then:
        response.status == HttpStatus.CREATED.value()

        and:
        JsonOutput.prettyPrint(response.contentAsString) == """
{
    "id": 1,
    "name": "test-name-post"
}
""".trim()
    }

    @TestConfiguration
    static class StubConfig {
        DetachedMockFactory detachedMockFactory = new DetachedMockFactory()

        @Bean
        SimpleService simpleService() {
            return detachedMockFactory.Stub(SimpleService)
        }
    }
}
