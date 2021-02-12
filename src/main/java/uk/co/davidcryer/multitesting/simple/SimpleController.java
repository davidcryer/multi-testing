package uk.co.davidcryer.multitesting.simple;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/simple")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SimpleController {
    private final SimpleService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SimpleRequest post(@RequestBody SimpleRequest request) {
        return service.add(request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SimpleRequest> get(@PathVariable Integer id) {
        return ResponseEntity.of(service.get(id));
    }
}
