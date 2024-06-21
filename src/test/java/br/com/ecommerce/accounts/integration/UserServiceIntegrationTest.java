package br.com.ecommerce.accounts.integration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import br.com.ecommerce.accounts.dto.AddressDTO;
import br.com.ecommerce.accounts.dto.LoginDTO;
import br.com.ecommerce.accounts.dto.TokenDTO;
import br.com.ecommerce.accounts.dto.UserClientDTO;
import br.com.ecommerce.accounts.dto.UserEmployeeDTO;
import br.com.ecommerce.accounts.exception.FailedCredentialsException;
import br.com.ecommerce.accounts.model.User;
import br.com.ecommerce.accounts.repository.UserRepository;
import br.com.ecommerce.accounts.service.UserService;
import jakarta.persistence.EntityNotFoundException;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceIntegrationTest {

    @Autowired
    private UserService service;
    @Autowired
    private UserRepository repository;
    @Autowired
    private BCryptPasswordEncoder encoder;


    @Test
    @DisplayName("Integration - saveUserClient - Must create a user client")
    void saveUserClientTest01() {
        // arrange
        UserClientDTO input = new UserClientDTO(
            "userclient-san",
            "password-san!",
            "Client-san",
            "client@email.com",
            "(11) 99999-9999",
            "123.456.789-00",
            new AddressDTO(null, null, null, null, null, null, null));

        // act
        var result = service.saveClientUser(input);
        
        // assert
        assertNotNull(result);
        assertNotNull(result.id());
        assertFalse(result.phone_number().isBlank());
        assertEquals(input.name(), result.name());
        assertEquals(input.email(), result.email());
        assertEquals(input.cpf(), result.cpf());
    }

    @Test
    @DisplayName("Integration - saveUserEmployee - Must create a user employee")
    void saveUserEmployeeTest01() {
        // arrange
        UserEmployeeDTO input = new UserEmployeeDTO(
            "userclient-san",
            "password-san!",
            "Client-san");

        // act
        var result = service.saveEmployeeUser(input);
        
        // assert
        assertNotNull(result.id());
        assertEquals(input.username(), result.username());
        assertEquals(input.name(), result.name());
    }

    @Test
    @DisplayName("Integration - auth - Must authenticate successfully and return a token in valid format")
    void authTest01() {
        // arrange
        String USERNAME = "test";
        String PASSWORD = "password";

        User user = User.builder().username(USERNAME).password(encoder.encode(PASSWORD)).build();
        repository.save(user);
        LoginDTO authData = new LoginDTO(USERNAME, PASSWORD);

        // act and assert
        assertDoesNotThrow(() -> {
            TokenDTO token = service.auth(authData);
            assertEquals(3, token.token().split("\\.").length, 
                "Valid token format must have three parts");
        });
    }
    @Test
    @DisplayName("Integration - auth - Must throw exceptions when passing invalid input")
    void authTest02() {
        // arrange
        String USERNAME = "test";
        String PASSWORD = "password";

        User user = User.builder().username(USERNAME).password(encoder.encode(PASSWORD)).build();
        repository.save(user);

        // act and assert
        assertThrows(EntityNotFoundException.class, () -> service.auth(new LoginDTO(PASSWORD, USERNAME)));
        assertThrows(FailedCredentialsException.class, () -> service.auth(new LoginDTO(USERNAME, PASSWORD+1)));
    }
}