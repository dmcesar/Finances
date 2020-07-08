package com.portfolio.financas.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.financas.api.dto.UserDTO;
import com.portfolio.financas.exceptions.AuthenticationException;
import com.portfolio.financas.exceptions.BusinessRuleException;
import com.portfolio.financas.model.entity.User;
import com.portfolio.financas.service.EntryService;
import com.portfolio.financas.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UserController.class) /* Raise REST context to test controller */
@AutoConfigureMockMvc /* Give access to MockMvc (Mock API calls) */
public class UserControllerTest {

    static final String API = "/api/users";
    static final MediaType JSON = MediaType.APPLICATION_JSON;

    @Autowired
    MockMvc mvc;

    @MockBean
    UserService userService;

    @MockBean
    EntryService entryService;

    @Test
    public void successfullyAuthenticateUserTest() throws Exception {

        String email = "test.user@email.com";
        String password = "abc123";

        /* Represents received JSON object */
        UserDTO dto = UserDTO.builder()
                .email(email)
                .password(password)
                .build();

        /* Represents authenticated user */
        User user = User.builder()
                .id(1L)
                .email(email)
                .password(password)
                .build();

        /* Mock authenticate() */
        Mockito.when(userService.authenticate(email, password)).thenReturn(user);

        /* Write DTO to json string */
        String json = new ObjectMapper().writeValueAsString(dto);

        /* Used to create requests.
         * Perform Post to API/authenticate.
         * Accept and send application/json content format.
         * Send json string parsed dto */
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API.concat("/authenticate"))
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        /* Perform request.
         * Expect HTTP 200 (OK) and user in json format with matching values. */
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(user.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value(user.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(user.getEmail()));
    }

    @Test
    public void authenticateUserInvalidCredentialsTest() throws Exception {

        String email = "test.user@email.com";
        String password = "abc123";

        /* Represents received JSON object */
        UserDTO dto = UserDTO.builder()
                .email(email)
                .password(password)
                .build();

        /* Mock authenticate() */
        Mockito.when(userService.authenticate(email, password)).thenThrow(AuthenticationException.class);

        /* Write DTO to json string */
        String json = new ObjectMapper().writeValueAsString(dto);

        /* Used to create requests.
         * Perform Post to API/authenticate.
         * Accept and send application/json content format.
         * Send json string parsed dto */
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API.concat("/authenticate"))
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        /* Perform request.
         * Expect HTTP Bad Request */
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void successfullyRegisterUserTest() throws Exception {

        String name = "test user";
        String email = "test.user@email.com";
        String password = "abc123";

        /* Represents received JSON object */
        UserDTO dto = UserDTO.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();

        /* Represents authenticated user */
        User user = User.builder()
                .id(1L)
                .name(name)
                .email(email)
                .password(password)
                .build();

        /* Mock register() */
        Mockito.when(userService.register(Mockito.any(User.class))).thenReturn(user);

        /* Write DTO to json string */
        String json = new ObjectMapper().writeValueAsString(dto);

        /* Used to create requests.
         * Perform Post to API/authenticate.
         * Accept and send application/json content format.
         * Send json string parsed dto */
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API.concat("/register"))
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        /* Perform request.
         * Expect HTTP 201 (CREATED) and user in json format with matching values. */
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(user.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("name").value(user.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("email").value(user.getEmail()));
    }

    @Test
    public void registerUserInvalidEmailErrorTest() throws Exception {

        String name = "test user";
        String email = "test.user@email.com";
        String password = "abc123";

        /* Represents received JSON object */
        UserDTO dto = UserDTO.builder()
                .name(name)
                .email(email)
                .password(password)
                .build();

        /* Mock register() */
        Mockito.when(userService.register(Mockito.any(User.class))).thenThrow(BusinessRuleException.class);

        /* Write DTO to json string */
        String json = new ObjectMapper().writeValueAsString(dto);

        /* Used to create requests.
         * Perform Post to API/authenticate.
         * Accept and send application/json content format.
         * Send json string parsed dto */
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(API.concat("/register"))
                .accept(JSON)
                .contentType(JSON)
                .content(json);

        /* Perform request.
         * Expect HTTP Bad Request and user in json format with matching values. */
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}