package uk.co.davidcryer.multitesting.large;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.davidcryer.multitesting.generated.tables.pojos.Large;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LargeService {
    private final LargeRepository repository;

    LargeRequest add(LargeRequest request) {
        var unsaved = toEntity(request);
        unsaved.setId(UUID.randomUUID().toString());
        var saved = repository.add(unsaved);
        return toRequest(saved);
    }

    private static Large toEntity(LargeRequest request) {
        return new Large(
                request.getId(),
                request.getFirst(),
                request.getSecond(),
                request.getThird(),
                request.getFourth(),
                request.getFifth(),
                request.getSixth(),
                request.getSeventh(),
                request.getEighth(),
                request.getNinth(),
                request.getTenth(),
                request.getEleventh(),
                request.getTwelfth(),
                request.getThirteenth(),
                request.getFourteenth(),
                request.getFifteenth(),
                request.getSixteenth(),
                request.getSeventeenth(),
                request.getEighteenth(),
                request.getNineteenth(),
                request.getTwentieth()
        );
    }

    Optional<LargeRequest> get(String id) {
        return repository.get(id).map(LargeService::toRequest);
    }

    private static LargeRequest toRequest(Large large) {
        return new LargeRequest(
                large.getId(),
                large.getFirst(),
                large.getSecond(),
                large.getThird(),
                large.getFourth(),
                large.getFifth(),
                large.getSixth(),
                large.getSeventh(),
                large.getEighth(),
                large.getNinth(),
                large.getTenth(),
                large.getEleventh(),
                large.getTwelfth(),
                large.getThirteenth(),
                large.getFourteenth(),
                large.getFifteenth(),
                large.getSixteenth(),
                large.getSeventeenth(),
                large.getEighteenth(),
                large.getNineteenth(),
                large.getTwentieth()
        );
    }
}
