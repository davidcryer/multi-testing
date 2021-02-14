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

    def addLetter(Letter letter) {
        def record = dslContext.newRecord(LETTER, letter)
        record.store()
        record.into Letter
    }

    Letter getLetter(String id) {
        dslContext
                .selectFrom(LETTER)
                .where(LETTER.ID.eq(id))
                .fetchOneInto Letter
    }

    def addAddress(Address address) {
        def record = dslContext.newRecord(ADDRESS, address)
        record.store()
        record.into Address
    }

    Address getAddress(String id) {
        dslContext
                .selectFrom(ADDRESS)
                .where(ADDRESS.ID.eq(id))
                .fetchOneInto Address
    }

    def delete(String id) {
        def letter = getLetter(id)
        dslContext
                .deleteFrom(LETTER)
                .where(LETTER.ID.eq(id))
                .execute()
        dslContext
                .deleteFrom(ADDRESS)
                .where(ADDRESS.ID.eq(letter.recipientAddress))
                .execute()
    }
}
