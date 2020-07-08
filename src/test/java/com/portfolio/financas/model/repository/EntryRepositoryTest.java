package com.portfolio.financas.model.repository;

import com.portfolio.financas.model.entity.Entry;
import com.portfolio.financas.model.enums.EntryStatus;
import com.portfolio.financas.model.enums.EntryType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class EntryRepositoryTest {

    @Autowired
    EntryRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void successfullyCreateEntryTest() {

        Entry entry = createEntry();

        /* Save entry in repository and retain persisted object */
        entry = repository.save(entry);

        /* Check if ID is not null (object must be persisted) */
        Assertions.assertThat(entry.getId()).isNotNull();
    }

    @Test
    public void successfullyDeleteEntryTest() {

        Entry entry = createEntry();

        /* Persist entry in DB */
        entityManager.persist(entry);

        /* Find persisted entry (with ID) */
        entry = entityManager.find(Entry.class, entry.getId());

        /* Delete persisted entry from repository (aka class to test) */
        repository.delete(entry);

        /* Find entity again */
        Entry deletedEntry = entityManager.find(Entry.class, entry.getId());

        /* Entry should now be null */
        Assertions.assertThat(deletedEntry).isNull();
    }

    @Test
    public void successfullyUpdateEntryTest() {

        Entry entry = createEntry();

        /* Persist entry in DB */
        entityManager.persist(entry);

        entry.setYear(2019);
        entry.setDescription("Test entry UPDATED");
        entry.setStatus(EntryStatus.CANCELED);

        /* Save updated entry */
        repository.save(entry);

        /* Retrieve updatedEntry */
        Entry updatedEntry = entityManager.find(Entry.class, entry.getId());

        /* Assert attributes  */
        Assertions.assertThat(updatedEntry.getYear()).isEqualTo(2019);
        Assertions.assertThat(updatedEntry.getDescription()).isEqualTo("Test entry UPDATED");
        Assertions.assertThat(updatedEntry.getStatus()).isEqualTo(EntryStatus.CANCELED);
    }

    @Test
    public void successfullyFindEntryByID() {

        Entry entry = createEntry();

        /* Persist entry in DB */
        entityManager.persist(entry);

        /* Retrieve persistedEntry */
        Optional<Entry> persistedEntry = repository.findById(entry.getId());

        /* Assert that entry is present */
        Assertions.assertThat(persistedEntry.isPresent()).isTrue();
    }

    public static Entry createEntry() {

        return Entry.builder()
                .year(2020)
                .month(7)
                .description("Test entry")
                .value(BigDecimal.valueOf(10))
                .type(EntryType.REVENUE)
                .status(EntryStatus.PENDING)
                .registryDate(LocalDate.now())
                .build();
    }
}
