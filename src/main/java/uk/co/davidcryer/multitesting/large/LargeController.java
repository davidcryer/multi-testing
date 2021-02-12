package uk.co.davidcryer.multitesting.large;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/large")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LargeController {
    private final LargeService service;

    @PostMapping
    @ResponseStatus(CREATED)
    public ResponseEntity<LargeRequest> post(@RequestBody LargeRequest request) {
        LargeRequest response = service.add(request);
        return ResponseEntity
                .created(URI.create("/large/" + response.getId()))
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LargeRequest> get(@PathVariable String id) {
        return ResponseEntity.of(service.get(id));
    }
}
