package com.portfolio.financas.service;

import com.portfolio.financas.model.entity.Entry;
import com.portfolio.financas.model.enums.EntryStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface EntryService {

    /* Creates a new Entry.
    * Returns persisted entry. */
    Entry create(Entry entry);

    /* Updates the given entry.
    * Returns updated entry. */
    Entry update(Entry entry);

    /* Deletes the given entry. */
    void delete(Entry entry);

    /* Returns list of entries that are similar to the given entry. */
    List<Entry> read(Entry filterEntry);

    /* Updates status of the given entry. */
    void updateStatus(Entry entry, EntryStatus status);

    /* Validates entry.
    * Throws exception if is invalid. */
    void validate(Entry entry);

    /* Returns Optional that may contain entry associated with the given ID. */
    Optional<Entry> getByID(Long id);

    /* Returns specified user's balance (REVENUES - EXPENSES) */
    BigDecimal getUserBalance(Long id);
}
