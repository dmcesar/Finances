package com.portfolio.financas.service;

import com.portfolio.financas.model.entity.User;

public interface UserService {

    User authenticate(String email, String password);

    User register(User user);

    void validateEmail(String email);
}
