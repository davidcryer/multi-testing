package uk.co.davidcryer.multitesting.labour;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/labour")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LabourController {
    private final LabourService service;

    @PostMapping
    public void post(@RequestBody FruitRequest fruit) {
        service.add(fruit);
    }
}
