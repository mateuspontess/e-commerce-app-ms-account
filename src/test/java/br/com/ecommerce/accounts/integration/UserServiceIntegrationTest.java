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

import br.com.ecommerce.accounts.exception.FailedCredentialsException;
import br.com.ecommerce.accounts.model.AddressDTO;
import br.com.ecommerce.accounts.model.LoginDTO;
import br.com.ecommerce.accounts.model.TokenDTO;
import br.com.ecommerce.accounts.model.User;
import br.com.ecommerce.accounts.model.UserClientDTO;
import br.com.ecommerce.accounts.model.UserEmployeeDTO;
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
    @DisplayName("Must create a user client")
    void createUserClientTest01() {
        // arrange
        UserClientDTO userClientDTO = new UserClientDTO(
            "userclient-san",
            "password-san!",
            "Client-san",
            "client@email.com",
            "(11) 99999-9999",
            "123.456.789-00",
            new AddressDTO(null, null, null, null, null, null, null));

        // act
        var result = service.createUserClient(userClientDTO);
        
        // assert
        assertNotNull(result);
        assertNotNull(result.id());
        assertFalse(result.phone_number().isBlank());
        assertEquals(userClientDTO.name(), result.name());
        assertEquals(userClientDTO.email(), result.email());
        assertEquals(userClientDTO.cpf(), result.cpf());
    }

    @Test
    @DisplayName("Must create a user employee")
    void createUserEmployeeTest01() {
        // arrange
        UserEmployeeDTO userEmployee = new UserEmployeeDTO(
            "userclient-san",
            "password-san!",
            "Client-san");

        // act
        var result = service.createUserEmployee(userEmployee);
        
        // assert
        assertNotNull(result.id());
        assertEquals(userEmployee.username(), result.username());
        assertEquals(userEmployee.name(), result.name());
    }

    @Test
    @DisplayName("Must authenticate successfully and return a token in valid format")
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
    @DisplayName("Must throw exceptions when passing invalid input")
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