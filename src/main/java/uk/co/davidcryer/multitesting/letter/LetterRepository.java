package uk.co.davidcryer.multitesting.letter;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uk.co.davidcryer.multitesting.generated.tables.pojos.Letter;

import static uk.co.davidcryer.multitesting.generated.Tables.LETTER;

@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LetterRepository {
    private final DSLContext dslContext;

    Letter add(Letter letter) {
        var record = dslContext.newRecord(LETTER, letter);
        record.store();
        return record.into(Letter.class);
    }
}
