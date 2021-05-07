package uk.co.davidcryer.multitesting.cv;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cvs")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CvController {
    private final CvService service;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<String> post(@RequestBody CvRequest cv) {
        if (StringUtils.isEmpty(cv.getEmailAddress())) {
            return ResponseEntity.badRequest().body("CV must contain email address");
        }
        var didSchedule = service.add(cv);
        if (didSchedule) {
            return ResponseEntity.accepted().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}
