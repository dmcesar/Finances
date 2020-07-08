package com.portfolio.financas.service;

import com.portfolio.financas.exceptions.BusinessRuleException;
import com.portfolio.financas.model.entity.Entry;
import com.portfolio.financas.model.entity.User;
import com.portfolio.financas.model.enums.EntryStatus;
import com.portfolio.financas.model.repository.EntryRepository;
import com.portfolio.financas.model.repository.EntryRepositoryTest;
import com.portfolio.financas.service.impl.EntryServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AssertionsKt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class EntryServiceTest {

    @SpyBean
    EntryServiceImpl service;

    @MockBean
    EntryRepository repository;

    @Test
    public void successfullyCreateEntryTest() {

        Entry entryToSave = EntryRepositoryTest.createEntry();

        /* Do not throw any exception when service.validate() is called with entryToSave */
        Mockito.doNothing().when(service).validate(entryToSave);

        Entry entrySaved = EntryRepositoryTest.createEntry();
        entrySaved.setId((long) 1);

        /* When repository.save() is called, return "dummy" entrySaved (with ID) */
        Mockito.when(repository.save(entryToSave)).thenReturn(entrySaved);

        /* Validate and save entry */
        Entry result = service.create(entryToSave);

        /* Assert that returned saved entry has expected ID */
        Assertions.assertThat(result.getId()).isEqualTo(entrySaved.getId());

        /* Assert that returned saved entry's status is PENDING */
        Assertions.assertThat(result.getStatus()).isEqualTo(EntryStatus.PENDING);
    }

    @Test
    public void createEntryValidationErrorTest() {

        Entry entryToSave = EntryRepositoryTest.createEntry();

        Mockito.doThrow(BusinessRuleException.class).when(service).validate(entryToSave);

        /* Assert thrown exception type */
        Assertions.catchThrowableOfType(() -> service.create(entryToSave), BusinessRuleException.class);

        /* Verify that repository.save() is never called */
        Mockito.verify(repository, Mockito.never()).save(entryToSave);
    }

    @Test
    public void successfullyUpdateEntryTest() {

        /* Create saved entry (with ID) */
        Entry entrySaved = EntryRepositoryTest.createEntry();
        entrySaved.setId((long) 1);

        /* Do not throw any exception when service.validate() is called with entrySaved */
        Mockito.doNothing().when(service).validate(entrySaved);

        /* When repository.save() is called, return "dummy" entrySaved (with ID) */
        Mockito.when(repository.save(entrySaved)).thenReturn(entrySaved);

        /* Validate and save entry */
        service.update(entrySaved);

        /* Verify that repository.save() (aka update entry) is called once */
        Mockito.verify(repository, Mockito.times(1)).save(entrySaved);
    }

    @Test
    public void updateEntryNotSavedErrorTest() {

        Entry entry = EntryRepositoryTest.createEntry();

        /* Assert thrown exception type */
        Assertions.catchThrowableOfType(() -> service.update(entry), NullPointerException.class);

        /* Verify that repository.save() is never called */
        Mockito.verify(repository, Mockito.never()).save(entry);
    }

    @Test
    public void successfullyDeleteEntryTest() {

        Entry entry = EntryRepositoryTest.createEntry();
        entry.setId((long) 1);

        service.delete(entry);

        Mockito.verify(repository).delete(entry);
    }

    @Test
    public void deleteEntryNotSavedErrorTest() {

        Entry entry = EntryRepositoryTest.createEntry();

        /* Assert thrown exception type */
        Assertions.catchThrowableOfType(() -> service.delete(entry), NullPointerException.class);

        /* Verify that repository.delete() is never called */
        Mockito.verify(repository, Mockito.never()).delete(entry);
    }

    @Test
    public void successfullyFilterEntriesTest() {

        Entry entry = EntryRepositoryTest.createEntry();
        entry.setId((long) 1);

        /* Create list containing entry */
        List<Entry> entries = Arrays.asList(entry);

        Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(entries);

        List<Entry> result = service.read(entry);

        Assertions.assertThat(result)
                .isNotEmpty()
                .hasSize(entries.size())
                .contains(entry);
    }

    @Test
    public void successfullyUpdateEntryStatusTest() {

        /* Create saved entry (with ID) */
        Entry entry = EntryRepositoryTest.createEntry();
        entry.setId((long) 1);
        entry.setStatus(EntryStatus.PENDING);

        /* Bypass entry validation */
        Mockito.doNothing().when(service).validate(entry);

        /* Call updateStatus() */
        service.updateStatus(entry, EntryStatus.CANCELED);

        /* Verify that entry was persisted */
        Mockito.verify(repository, Mockito.times(1)).save(entry);
    }

    @Test
    public void successfullyGetEntryByID() {

        Long id = 1L;

        Entry entry = EntryRepositoryTest.createEntry();
        entry.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(entry));

        Optional<Entry> result = service.getByID(id);

        Assertions.assertThat(result.isPresent()).isTrue();
    }

    @Test
    public void getNonexistentEntryByIDErrorTest() {

        Long id = 1L;

        Entry entry = EntryRepositoryTest.createEntry();
        entry.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Entry> result = service.getByID(id);

        Assertions.assertThat(result.isPresent()).isFalse();
    }

    @Test
    public void validateEntryErrorsTest() {

        Entry entry = new Entry();

        Throwable error = Assertions.catchThrowable(() -> service.validate(entry));
        Assertions.assertThat(error)
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Invalid entry Description.");

        entry.setDescription("Description");

        error = Assertions.catchThrowable(() -> service.validate(entry));
        Assertions.assertThat(error)
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Invalid Month value.");

        entry.setMonth(7);

        error = Assertions.catchThrowable(() -> service.validate(entry));
        Assertions.assertThat(error)
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Invalid Year value.");

        entry.setYear(2020);

        error = Assertions.catchThrowable(() -> service.validate(entry));
        Assertions.assertThat(error)
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("User must be associated.");

        entry.setUser(new User());
        entry.getUser().setId((long) 1);

        error = Assertions.catchThrowable(() -> service.validate(entry));
        Assertions.assertThat(error)
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Must insert value above 0.");

        entry.setValue(BigDecimal.TEN);

        error = Assertions.catchThrowable(() -> service.validate(entry));
        Assertions.assertThat(error)
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Must associate a type.");
    }
}