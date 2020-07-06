package com.portfolio.financas.service.impl;

import com.portfolio.financas.exceptions.AuthenticationException;
import com.portfolio.financas.exceptions.BusinessRuleException;
import com.portfolio.financas.model.entity.User;
import com.portfolio.financas.model.repository.UserRepository;
import com.portfolio.financas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    /* Checks whether user exists. If user exists, compares passwords. Returns user if credentials match or exception. */
    @Override
    public User authenticate(String email, String password) {

        Optional<User> user = repository.findByEmail(email);

        if(!user.isPresent()) {

            throw new AuthenticationException("User not found.");
        }

        if(!user.get().getPassword().equals(password)) {

            throw new AuthenticationException("Invalid password.");
        }

        return user.get();
    }

    /* Validates new user's email. If email is valid, persists and returns user with associated ID. */
    @Override
    @Transactional /* Commits changes to DB after operation is successful */
    public User register(User user) {

        validateEmail(user.getEmail());

        return repository.save(user);
    }

    /* Throws exception if there is already an user with the given email. */
    @Override
    public void validateEmail(String email) {

        boolean exists = repository.existsByEmail(email);

        if(exists) {

            throw new BusinessRuleException("A user already exists with the given email.");
        }
    }

    @Override
    public Optional<User> getByID(Long id) {

        return repository.findById(id);
    }
}