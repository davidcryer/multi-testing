package uk.co.davidcryer.multitesting.letter

import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import uk.co.davidcryer.multitesting.generated.tables.pojos.Address
import uk.co.davidcryer.multitesting.generated.tables.pojos.Letter

import static uk.co.davidcryer.multitesting.generated.Tables.ADDRESS
import static uk.co.davidcryer.multitesting.generated.Tables.LETTER

@Component
class LetterDbOps {
    @Autowired
    private DSLContext dslContext

    Letter getLetter(String id) {
        dslContext
                .selectFrom(LETTER)
                .where(LETTER.ID.eq(id))
                .fetchOneInto Letter
    }

    Address getRecipientAddress(String recipientAddressId) {
        dslContext
                .selectFrom(ADDRESS)
                .where(ADDRESS.ID.eq(recipientAddressId))
                .fetchOneInto Address
    }

    def delete(String id) {
        dslContext
                .deleteFrom(LETTER)
                .where(LETTER.ID.eq(id))
                .execute()
    }
}
