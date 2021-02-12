package uk.co.davidcryer.multitesting.simple;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.davidcryer.multitesting.generated.tables.pojos.Simple;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SimpleService {
    private final SimpleRepository repository;

    SimpleRequest add(SimpleRequest request) {
        var unsavedSimple = new Simple();
        unsavedSimple.setName(request.getName());
        var savedSimple = repository.add(unsavedSimple);
        return toRequest(savedSimple);
    }

    Optional<SimpleRequest> get(Integer id) {
        return repository.get(id).map(SimpleService::toRequest);
    }

    private static SimpleRequest toRequest(Simple simple) {
        return new SimpleRequest(simple.getId(), simple.getName());
    }
}
