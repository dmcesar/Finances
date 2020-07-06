package com.portfolio.financas.model.repository;

import com.portfolio.financas.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /* Returns if there is any user associated with email. */
    boolean existsByEmail(String email);

    /* Return persisted user with associated email or returns without user. */
    Optional<User> findByEmail(String email);
}
