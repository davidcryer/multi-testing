package uk.co.davidcryer.multitesting.simple;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.davidcryer.multitesting.generated.tables.pojos.Simple;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SimpleService {
    private final SimpleRepository repository;

    public SimpleRequest create(SimpleRequest request) {
        Simple unsavedSimple = new Simple();
        unsavedSimple.setName(request.getName());
        Simple savedSimple = repository.create(unsavedSimple);
        return new SimpleRequest(savedSimple.getId(), savedSimple.getName());
    }
}
