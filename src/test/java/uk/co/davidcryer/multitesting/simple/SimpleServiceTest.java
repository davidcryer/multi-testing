package uk.co.davidcryer.multitesting.simple;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.davidcryer.multitesting.generated.tables.pojos.Simple;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SimpleServiceTest {
    @Mock
    private SimpleRepository repository;
    @InjectMocks
    private SimpleService service;

    @Test
    public void add() {
        when(repository.add(any())).thenReturn(new Simple(1, "test-name"));

        var response = service.add(new SimpleRequest(null, "test-name"));

        assertThat(response).isEqualTo(new SimpleRequest(1, "test-name"));
        verify(repository).add(new Simple(null, "test-name"));
    }

    @Test
    public void add_ignoresIdInRequest() {
        when(repository.add(any())).thenReturn(new Simple());

        service.add(new SimpleRequest(-1, null));

        verify(repository).add(new Simple());
    }

    @Test
    public void get() {
        when(repository.get(1)).thenReturn(Optional.of(new Simple(1, "test-name")));

        var response = service.get(1);

        assertThat(response).isEqualTo(Optional.of(new SimpleRequest(1, "test-name")));
    }

    @Test
    public void get_returnsEmptyIfNoEntity() {
        when(repository.get(1)).thenReturn(Optional.empty());

        var response = service.get(1);

        assertThat(response).isEqualTo(Optional.empty());
    }
}
