package uk.co.davidcryer.multitesting.simple;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/simple")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SimpleController {
    private final SimpleService simpleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SimpleRequest create(@RequestBody SimpleRequest request) {
        return simpleService.create(request);
    }
}
