package uk.co.davidcryer.multitesting.letter;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/letter")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LetterController {
    private final LetterService service;

    @GetMapping("/{id}")
    public ResponseEntity<LetterMessage> get(@PathVariable String id) {
        return ResponseEntity.of(service.get(id));
    }
}
