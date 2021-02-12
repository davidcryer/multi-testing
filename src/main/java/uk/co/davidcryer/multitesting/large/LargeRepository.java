package uk.co.davidcryer.multitesting.large;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uk.co.davidcryer.multitesting.generated.tables.pojos.Large;

import java.util.Optional;

import static uk.co.davidcryer.multitesting.generated.Tables.LARGE;

@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LargeRepository {
    private final DSLContext dslContext;

    public Large add(Large large) {
        var record = dslContext.newRecord(LARGE, large);
        record.store();
        return record.into(Large.class);
    }

    public Optional<Large> get(String id) {
        return Optional.ofNullable(
                dslContext
                        .selectFrom(LARGE)
                        .where(LARGE.ID.eq(id))
                        .fetchOneInto(Large.class)
        );
    }
}
