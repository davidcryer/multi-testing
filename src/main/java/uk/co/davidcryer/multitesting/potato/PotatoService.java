package uk.co.davidcryer.multitesting.potato;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PotatoService {
    private final PotatoClient client;

    void add(PotatoMessage message) {
        var request = new PotatoClientRequest(
                message.getId(),
                message.getType(),
                message.getDescription(),
                message.getTemperature().getValue() + message.getTemperature().getUnit()
        );
        client.put(request);
    }
}
