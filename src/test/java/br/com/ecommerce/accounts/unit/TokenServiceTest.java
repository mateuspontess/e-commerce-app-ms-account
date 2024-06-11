package br.com.ecommerce.accounts.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.ecommerce.accounts.model.User;
import br.com.ecommerce.accounts.service.TokenService;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
	
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
    void generateToken() {
        // act
        var result = tokenService.generateToken(user);

        // assert
        assertTrue(isValidTokenFormat(result));
    }

    private boolean isValidTokenFormat(String token) {
		var parts = Arrays.asList(token.split("\\."));

		if(parts.size() != 3) 
            return false;

		try {
            parts.forEach(part -> Base64.getUrlDecoder().decode(part));
			return true; 

		} catch (IllegalArgumentException ex) {
			return false;
		}
	}
}