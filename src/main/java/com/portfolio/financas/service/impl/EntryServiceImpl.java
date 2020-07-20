package com.portfolio.financas.service.impl;

import com.portfolio.financas.exceptions.BusinessRuleException;
import com.portfolio.financas.model.entity.Entry;
import com.portfolio.financas.model.enums.EntryStatus;
import com.portfolio.financas.model.enums.EntryType;
import com.portfolio.financas.model.repository.EntryRepository;
import com.portfolio.financas.service.EntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class EntryServiceImpl implements EntryService {

    private EntryRepository repository;

    @Autowired
    public EntryServiceImpl(EntryRepository repository) {

        this.repository = repository;
    }

    @Override
    @Transactional
    public Entry create(Entry entry) {

        /* Validate form entry */
        validate(entry);

        /* Update status */
        entry.setStatus(EntryStatus.PENDING);

        /* Persist entry */
        return repository.save(entry);
    }

    @Override
    @Transactional
    public Entry update(Entry entry) {

        /* Given entry must have an ID (must already be persisted in DB) or NPE is thrown  */
        Objects.requireNonNull(entry.getId());

        /* Validate form entry */
        validate(entry);

        /* Persist entry */
        return repository.save(entry);
    }

    @Override
    @Transactional
    public void delete(Entry entry) {

        /* Given entry must have an ID (must already be persisted in DB) or NPE is thrown  */
        Objects.requireNonNull(entry.getId());

        /* Persist entry */
        repository.delete(entry);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Entry> read(Entry filterEntry) {

        /* Object used to query DB */
        Example<Entry> example = Example.of(
                filterEntry,
                ExampleMatcher.matching()
                        .withIgnoreCase()
                        .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));

        return repository.findAll(example);
    }

    @Override
    @Transactional
    public void updateStatus(Entry entry, EntryStatus status) {

        /* Update status */
        entry.setStatus(status);

        /* Persist entry */
        update(entry);
    }

    @Override
    public void validate(Entry entry) {

        if(entry.getDescription() == null || entry.getDescription().trim().equals("")) {

            throw new BusinessRuleException("Invalid entry Description.");
        }

        if(entry.getMonth() == null || entry.getMonth() < 1 || entry.getMonth() > 12) {

            throw new BusinessRuleException("Invalid Month value.");
        }

        if(entry.getYear() == null || entry.getYear().toString().length() != 4) {

            throw new BusinessRuleException("Invalid Year value.");
        }

        if(entry.getUser() == null || entry.getUser().getId() == null) {

            throw new BusinessRuleException("User must be associated.");
        }

        if(entry.getValue() == null || entry.getValue().compareTo(BigDecimal.ZERO) < 1) {

            throw new BusinessRuleException("Must insert value above 0.");
        }

        if(entry.getType() == null) {

            throw new BusinessRuleException("Must associate a type.");
        }
    }

    @Override
    public Optional<Entry> getByID(Long id) {

        return repository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getUserBalance(Long id) {

        BigDecimal revenue = repository.getValueByUserAndEntryTypeAndStatus(id, EntryType.REVENUE, EntryStatus.EFFECTED);
        BigDecimal expenses = repository.getValueByUserAndEntryTypeAndStatus(id, EntryType.EXPENSE, EntryStatus.EFFECTED);

        if(revenue == null) {

            revenue = BigDecimal.ZERO;
        }

        if(expenses == null) {

            expenses = BigDecimal.ZERO;
        }

        return revenue.subtract(expenses);
    }
}
