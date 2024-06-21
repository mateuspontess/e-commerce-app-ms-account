package br.com.ecommerce.accounts.unit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.ecommerce.accounts.dto.AddressDTO;
import br.com.ecommerce.accounts.dto.LoginDTO;
import br.com.ecommerce.accounts.dto.UserClientDTO;
import br.com.ecommerce.accounts.dto.UserEmployeeDTO;
import br.com.ecommerce.accounts.exception.FailedCredentialsException;
import br.com.ecommerce.accounts.model.User;
import br.com.ecommerce.accounts.repository.UserRepository;
import br.com.ecommerce.accounts.service.TokenService;
import br.com.ecommerce.accounts.service.UserService;
import br.com.ecommerce.accounts.utils.TokenFormatValidatorUtils;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private TokenService tokenService = new TokenService();

	@Mock
	private UserRepository repository;
	@InjectMocks
	private UserService service;


	@Test
    @DisplayName("Unit - createUserEmployee - Must create a user client")
    void createUserClientTest01() {
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
        assertFalse(result.phone_number().isBlank());
        assertEquals(input.name(), result.name());
        assertEquals(input.email(), result.email());
        assertEquals(input.cpf(), result.cpf());
    }

    @Test
    @DisplayName("Unit - createUserEmployee - Must create a user employee")
    void createUserEmployeeTest01() {
        // arrange
        UserEmployeeDTO input = new UserEmployeeDTO(
            "userclient-san",
            "password-san!",
            "Client-san");

        // act
        var result = service.saveEmployeeUser(input);
        
        // assert
        assertEquals(input.username(), result.username());
        assertEquals(input.name(), result.name());
    }
    
    @Test
    @DisplayName("Unit - auth - Must authenticate successfully and return a token in valid format")
    void authTest01() {
        // arrange
        ReflectionTestUtils.setField(service, "encoder", this.encoder);
        ReflectionTestUtils.setField(service, "tokenService", this.tokenService);
        ReflectionTestUtils.setField(tokenService, "secret", "secret");

        String PASSWORD = "123";
        User userMock = User.builder().password(encoder.encode(PASSWORD)).build();
        when(repository.findByUsername(anyString())).thenReturn(Optional.of(userMock));
        
        // act and assert
        LoginDTO validInput = new LoginDTO("anything", PASSWORD);
        assertDoesNotThrow(() -> {
            var result =  service.auth(validInput);
            assertTrue(TokenFormatValidatorUtils.isValidTokenFormat(result.token()));
        });
    }
    @Test
    @DisplayName("Unit - auth - Should throw exceptions when not finding the user")
    void authTest02() {
        // arrange
        ReflectionTestUtils.setField(service, "encoder", this.encoder);
        ReflectionTestUtils.setField(service, "tokenService", this.tokenService);
        ReflectionTestUtils.setField(tokenService, "secret", "secret");

        // simulates an unsuccessful query by the user
        
        // act and assert
        String INVALID_PASSWORD = "1234";
        LoginDTO invalidInput = new LoginDTO("anything", INVALID_PASSWORD);
        assertThrows(EntityNotFoundException.class, () -> service.auth(invalidInput));
    }
    @Test
    @DisplayName("Unit - auth - Must throw exceptions when passing invalid input")
    void authTest03() {
        // arrange
        ReflectionTestUtils.setField(service, "encoder", this.encoder);
        ReflectionTestUtils.setField(service, "tokenService", this.tokenService);
        ReflectionTestUtils.setField(tokenService, "secret", "secret");

        String PASSWORD = "123";
        User userMock = User.builder().password(encoder.encode(PASSWORD)).build();
        when(repository.findByUsername(anyString())).thenReturn(Optional.of(userMock));
        
        // act and assert
        String INVALID_PASSWORD = "1234";
        LoginDTO invalidInput = new LoginDTO("anything", INVALID_PASSWORD);
        assertThrows(FailedCredentialsException.class, () -> service.auth(invalidInput));
    }
}