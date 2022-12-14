package uk.co.davidcryer.multitesting.cv;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uk.co.davidcryer.multitesting.generated.tables.pojos.Cv;
import uk.co.davidcryer.multitesting.generated.tables.records.CvRecord;

import java.util.Optional;
import java.util.function.Consumer;

import static uk.co.davidcryer.multitesting.generated.Tables.CV;

@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CvRepository {
    private final DSLContext dslContext;

    public Cv add(Cv cv) {
        var record = dslContext.newRecord(CV, cv);
        record.store();
        return record.into(Cv.class);
    }

    public Optional<Cv> get(String id) {
        return Optional.ofNullable(
                dslContext
                        .selectFrom(CV)
                        .where(CV.ID.eq(id))
                        .fetchOneInto(Cv.class)
        );
    }

    public void update(String id, Consumer<CvRecord> cvMutator) {
        var cv = dslContext.selectFrom(CV)
                .where(CV.ID.eq(id))
                .fetchOne();
        cvMutator.accept(cv);
        cv.store();
    }
}