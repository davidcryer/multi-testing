package uk.co.davidcryer.multitesting.letter;

import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jooq.JooqTest;
import org.springframework.dao.DataAccessException;
import uk.co.davidcryer.multitesting.generated.tables.pojos.Address;
import uk.co.davidcryer.multitesting.generated.tables.pojos.Letter;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.co.davidcryer.multitesting.generated.Tables.ADDRESS;
import static uk.co.davidcryer.multitesting.generated.Tables.LETTER;

@JooqTest
public class LetterAndAddressSchemaTest {
    @Autowired
    private DSLContext dslContext;

    @Test
    public void letter_insert_idIsSaved() {
        var letter = new Letter();
        letter.setId("id");
        dslContext.newRecord(LETTER, letter).store();
    }

    @Test
    public void letter_insert_acceptsRandomUUID() {
        var letter = new Letter();
        letter.setId(UUID.randomUUID().toString());
        dslContext.newRecord(LETTER, letter).store();
    }

    @Test
    public void letter_insert_referencesAddress() {
        var address = new Address();
        address.setId("addressId");
        address.setPostcode("required");
        var addressRecord = dslContext.newRecord(ADDRESS, address);
        addressRecord.store();
        var letter = new Letter();
        letter.setId("letterId");
        letter.setRecipientAddress(addressRecord.getId());
        dslContext.newRecord(LETTER, letter).store();
    }

    @Test
    public void letter_insert_errorsIfAddressReferenceBroken() {
        assertThrows(DataAccessException.class, () -> {
            var letter = new Letter();
            letter.setId("id");
            letter.setRecipientAddress("broken reference");
            dslContext.newRecord(LETTER, letter).store();
        });
    }

    @Test
    public void address_insert_idIsSaved() {
        var address = new Address();
        address.setId("id");
        address.setPostcode("required");
        dslContext.newRecord(ADDRESS, address).store();
    }

    @Test
    public void address_insert_acceptsRandomUUID() {
        var address = new Address();
        address.setId(UUID.randomUUID().toString());
        address.setPostcode("required");
        dslContext.newRecord(ADDRESS, address).store();
    }

    @Test
    public void address_insert_errorsIfPostcodeIsNull() {
        assertThrows(DataAccessException.class, () -> {
            var address = new Address();
            address.setId("id");
            dslContext.newRecord(ADDRESS, address).store();
        });
    }
}
