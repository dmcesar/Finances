package com.portfolio.financas.model.repository;

import com.portfolio.financas.model.entity.Entry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntryRepository extends JpaRepository<Entry, Long> {

}
