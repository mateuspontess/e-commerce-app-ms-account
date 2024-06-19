package br.com.ecommerce.accounts.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.ecommerce.accounts.model.User;
import br.com.ecommerce.accounts.service.TokenService;
import br.com.ecommerce.accounts.utils.TokenFormatValidatorUtils;

@ExtendWith(MockitoExtension.class)
class TokenServiceUnitTest {
	
    @InjectMocks
    private TokenService tokenService;

    private String secret = "testSecret";
    private final User user = User.builder()
        .username("default")
        .build();

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(tokenService, "secret", secret);
    }

    @Test
    void generateTokenTest() {
        // act
        var result = tokenService.generateToken(user);

        // assert
        assertTrue(TokenFormatValidatorUtils.isValidTokenFormat(result));
    }
}