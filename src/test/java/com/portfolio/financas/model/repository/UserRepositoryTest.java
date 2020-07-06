package com.portfolio.financas.model.repository;

import com.portfolio.financas.model.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

/* Performs integration testing with user table in DB */
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test") /* Uses test profile */
@DataJpaTest /* Rollback repository after running each test */
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) /* Configures DB with configurations */
public class UserRepositoryTest {

    /* Class being tested. Any operations effected to entityManager should take effect here. */
    @Autowired
    UserRepository repository;

    /* Responsible for DB operations (selects, inserts, deletes, updates). Commits rollback after each test. */
    @Autowired
    TestEntityManager entityManager;

    /* Should equals true since user is already registered */
    @Test
    public void validateEmailExistsTest() {

        User user = createUser();

        entityManager.persist(user);

        boolean result = repository.existsByEmail("test.user@email.com");

        Assertions.assertThat(result).isTrue();
    }

    /* Should return false since no user is registered with that email */
    @Test
    public void validateMailNotExistsTest() {

        boolean result = repository.existsByEmail("test.user@email.com");

        Assertions.assertThat(result).isFalse();
    }

    /* Persists user to DB and returns not null ID */
    @Test
    public void persistUserTest() {

        User user = createUser();

        User persistedUser = entityManager.persist(user);

        Assertions.assertThat(persistedUser.getId()).isNotNull();
    }

    /* Should return user associated to email */
    @Test
    public void findExistingUserByEmailTest() {

        User user = createUser();

        entityManager.persist(user);

        Optional<User> result = repository.findByEmail("test.user@email.com");

        Assertions.assertThat(result.isPresent()).isTrue();
    }

    /* Response should be empty since there is no user associated to email */
    @Test
    public void findNonExistingUserByEmailTest() {

        Optional<User> result = repository.findByEmail("test.user@email.com");

        Assertions.assertThat(result.isPresent()).isFalse();
    }

    /* Returns default testing object */
    public static User createUser() {

        return User.builder()
                .name("Test User")
                .email("test.user@email.com")
                .password("abc123")
                .build();
    }
}