package com.portfolio.financas.model.repository;

import com.portfolio.financas.model.entity.Entry;
import com.portfolio.financas.model.enums.EntryStatus;
import com.portfolio.financas.model.enums.EntryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface EntryRepository extends JpaRepository<Entry, Long> {

    @Query(value =
            "SELECT SUM(e.value) FROM Entry e JOIN e.user u " +
            "WHERE u.id = :userID AND e.type = :entryType AND e.status = :status GROUP BY u" )
    BigDecimal getValueByUserAndEntryTypeAndStatus
            (@Param("userID") Long userID,
             @Param("entryType") EntryType entryType,
             @Param("status") EntryStatus status);
}

