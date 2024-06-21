package br.com.ecommerce.accounts.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import br.com.ecommerce.accounts.model.User;
import br.com.ecommerce.accounts.model.UserBuilder;

public class UserBuilderUnitTest {

    UserBuilder test = new UserBuilder();

    private final String bcrypPattern = "\\$2[aby]?(\\$.{56})";

    
    @Test
    void createUserClientPasswordIsEncryptedAndPhoneNumberIsFormattedTest() {
        User target = test.createUserClient(null, "password", null, null, "47 999999999", null, null);

        var ENCODED_PASSWORD = target.getPassword();
        var FORMATED_PHONE_NUMBER = target.getPhone_number();

        assertTrue(ENCODED_PASSWORD.matches(bcrypPattern));
        assertEquals("+55 47 99999-9999", FORMATED_PHONE_NUMBER);
    }
    @Test
    void createUserEmployeePasswordIsEncryptedTest() {
        User target = test.createUserEmployee(null, "password", null);
        assertTrue(target.getPassword().matches(bcrypPattern));
    }
    @Test
    void createUserAdminPasswordIsEncryptedTest() {
        User target = test.createUserAdmin(null, "password", null);
        assertTrue(target.getPassword().matches(bcrypPattern));
    }
}