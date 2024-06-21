package br.com.ecommerce.accounts.model;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import lombok.NoArgsConstructor;


@NoArgsConstructor
public class UserBuilder {
    
    public User createUserClient(String username, String password, String name, String email, String phone_number, String cpf, Address address) {
        return new User(null, username, encodePassword(password), name, email, formatPhoneNumber(phone_number), cpf, address, UserRole.CLIENT);
    }
    public User createUserEmployee(String username, String password, String name) {
        return new User(null, username, encodePassword(password), name, null, null, null, null, UserRole.EMPLOYEE);
    }
    public User createUserAdmin(String username, String password, String name) {    	
        return new User(null, username, encodePassword(password), name, null, null, null, null, UserRole.ADMIN);
    }
    
    private String formatPhoneNumber(String phoneNumber) {
    	PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
    	PhoneNumber phoneNumberObject = null;
    	
    	try {
    		phoneNumberObject = phoneNumberUtil.parse(phoneNumber, "BR");
			
		} catch (NumberParseException e) {
			e.printStackTrace();
			throw new RuntimeException("Conversion failed: phone number");
		}
    	
        return phoneNumberUtil.format(phoneNumberObject, PhoneNumberFormat.INTERNATIONAL);
    }
    
    private String encodePassword(String password) {
    	return new BCryptPasswordEncoder().encode(password);
    }
}