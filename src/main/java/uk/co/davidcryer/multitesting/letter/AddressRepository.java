package uk.co.davidcryer.multitesting.letter;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uk.co.davidcryer.multitesting.generated.tables.pojos.Address;

import static uk.co.davidcryer.multitesting.generated.Tables.ADDRESS;

@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AddressRepository {
    private final DSLContext dslContext;

    Address add(Address address) {
        var record = dslContext.newRecord(ADDRESS, address);
        record.store();
        return record.into(Address.class);
    }
}
