package br.com.ecommerce.accounts.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import br.com.ecommerce.accounts.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase
class UserAdminCreationTest {

    @Autowired
    private UserRepository repository;
    @Value("${user.admin.username}")
	private String username;
    @Value("${user.admin.password}")
	private String password;
    @Autowired
    private BCryptPasswordEncoder encoder;


    @Test
    @DisplayName("Integration - verifyIfAdminIsPersisted")
    void verifyIfUserAdminIsPersistedTest() {
        // act
        var admin = repository.findByUsername(username);
        
        // assert
        assertNotNull(admin.get());
        assertTrue(encoder.matches(password, admin.get().getPassword()));
    }
}