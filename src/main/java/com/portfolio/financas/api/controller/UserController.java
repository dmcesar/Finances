package com.portfolio.financas.api.controller;

import com.portfolio.financas.api.dto.UserDTO;
import com.portfolio.financas.exceptions.AuthenticationException;
import com.portfolio.financas.exceptions.BusinessRuleException;
import com.portfolio.financas.model.entity.User;
import com.portfolio.financas.service.EntryService;
import com.portfolio.financas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;
    private EntryService entryService;

    @Autowired
    public UserController(UserService userService, EntryService entryService) {

        this.userService = userService;
        this.entryService = entryService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity authenticate(@RequestBody UserDTO dto) {

        try {

            User persistedUser = userService.authenticate(dto.getEmail(), dto.getPassword());

            return ResponseEntity.ok(persistedUser);

        } catch (AuthenticationException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody UserDTO dto) {

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .build();

        try {

            User persistedUser = userService.register(user);

            return new ResponseEntity(persistedUser, HttpStatus.CREATED);

        } catch (BusinessRuleException e) {

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("{id}/balance")
    public ResponseEntity getBalance(@PathVariable("id") Long id) {

        Optional<User> user = userService.getByID(id);

        if(!user.isPresent()) {

            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        BigDecimal balance = entryService.getUserBalance(id);

        return ResponseEntity.ok(balance);
    }
}
