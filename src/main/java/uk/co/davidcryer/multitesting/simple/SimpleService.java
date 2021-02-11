package uk.co.davidcryer.multitesting.simple;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.davidcryer.multitesting.generated.tables.pojos.Simple;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SimpleService {
    private final SimpleRepository repository;

    SimpleRequest create(SimpleRequest request) {
        var unsavedSimple = new Simple();
        unsavedSimple.setName(request.getName());
        var savedSimple = repository.create(unsavedSimple);
        return toRequest(savedSimple);
    }

    SimpleRequest get(Integer id) {
        var simple = repository.get(id);
        return toRequest(simple);
    }

    private static SimpleRequest toRequest(Simple simple) {
        return new SimpleRequest(simple.getId(), simple.getName());
    }
}
