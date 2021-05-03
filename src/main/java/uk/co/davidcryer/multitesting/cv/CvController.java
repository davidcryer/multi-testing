package uk.co.davidcryer.multitesting.cv;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cvs")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CvController {
    private final CvService service;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void post(@RequestBody CvRequest cv) {
        service.add(cv);
    }
}
