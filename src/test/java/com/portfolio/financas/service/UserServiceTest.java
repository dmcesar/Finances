package com.portfolio.financas.service;

import com.portfolio.financas.exceptions.AuthenticationException;
import com.portfolio.financas.exceptions.BusinessRuleException;
import com.portfolio.financas.model.entity.User;
import com.portfolio.financas.model.repository.UserRepository;
import com.portfolio.financas.service.impl.UserServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

/* Performs unit tests to user service using Mock and Spy */
@ExtendWith(SpringExtension.class)
/* Uses test profile */
@ActiveProfiles("test")
public class UserServiceTest {

    /* Create spy for service with spring context. Calls original methods but returns specified value. */
    @SpyBean
    UserServiceImpl service;

    /* Mocks repository with spring context. Calls fake methods and returns specified values. */
    @MockBean
    UserRepository repository;

    /* Validates that no user exists with associated email. Should not throw an exception. */
    @Test
    public void validateEmail() {

        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

        try {

            service.validateEmail("test.user@email.com");

        } catch (Exception e) {

            Assertions.fail(e.getMessage());
        }
    }

    /* Inserts an user and validates email associated with that user. Should throw an exception. */
    @Test
    public void validateEmailFailure() {

        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

        Assertions.assertThatExceptionOfType(BusinessRuleException.class).isThrownBy(() -> service.validateEmail("test.user@email.com"));
    }

    /* Tests valid user authentication */
    @Test
    public void validAuthenticationTest() {

        String email = "test.user@email.com";
        String password = "abc123";

        User user = User.builder()
                .email(email)
                .password(password)
                .id(1)
                .build();

        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = service.authenticate(email, password);

        Assertions.assertThat(result).isNotNull();
    }

    /* Tests user authentication with unknown email  */
    @Test
    public void unknownEmailAuthenticationTest() {

        String email = "test.user@email.com";
        String password = "abc123";

        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.authenticate(email, password)).isInstanceOf(AuthenticationException.class);
    }

    /* Tests user authentication with invalid password */
    @Test
    public void invalidPasswordAuthenticationTest() {

        String email = "test.user@email.com";
        String password = "abc123";

        User user = User.builder()
                .email(email)
                .password(password)
                .id(1)
                .build();

        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(user));

        Assertions.assertThatThrownBy(() -> service.authenticate(email, "123abc")).isInstanceOf(AuthenticationException.class);
    }

    /* Test successful user creation */
    @Test
    public void successfullyCreateUserTest() {

        /* Mock UserService.validateEmail() to return nothing (no exception is thrown) */
        Mockito.doNothing().when(service).validateEmail(Mockito.anyString());

        /* Persisted user to be returned */
        User user = User.builder()
                .id(1)
                .email("test.user@email.com")
                .name("user")
                .password("abc123")
                .build();

        /* Mock UserRepository.save() to return persisted user */
        Mockito.when(repository.save(Mockito.any(User.class))).thenReturn(user);

        /* Register an user and obtain persisted (mocked) result */
        User result = service.register(new User());

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getId()).isEqualTo(1);
    }

    /* Test unsuccessful user creation because email is already registered */
    @Test
    public void createUserErrorTest() {

        Mockito.doThrow(BusinessRuleException.class).when(service).validateEmail(Mockito.anyString());

        User user = User.builder()
                .email("test.user@email.com")
                .build();

        Assertions.assertThatThrownBy(() -> service.register(user)).isInstanceOf(BusinessRuleException.class);
    }
}