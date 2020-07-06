package com.portfolio.financas.service;

import com.portfolio.financas.model.entity.User;

import java.util.Optional;

public interface UserService {

    User authenticate(String email, String password);

    User register(User user);

    void validateEmail(String email);

    Optional<User> getByID(Long id);
}
