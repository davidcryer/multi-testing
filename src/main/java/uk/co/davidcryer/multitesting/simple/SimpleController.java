package uk.co.davidcryer.multitesting.simple;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/simple")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SimpleController {
    private final SimpleService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SimpleRequest create(@RequestBody SimpleRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public SimpleRequest get(@PathVariable Integer id) {
        return service.get(id);
    }
}
