package br.com.ecommerce.accounts.model;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserFactory {

    private Long id;
    private String username;
    private String password;
    private String name;
    private String email;
    private String phone_number;
    private String cpf;
    private Address address;
    private UserRole role;

    
    public UserFactory createUserClient(String username, String password, String name, String email, String phone_number, String cpf, Address address, UserRole role) {
    	this.password = this.encodePassword(password);
    	this.phone_number = this.formatPhoneNumber(phone_number);
    	
    	this.username = username;
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.role = UserRole.CLIENT;
        
        this.address = new Address(
        		address.getStreet(),
        		address.getNeighborhood(),
        		address.getPostal_code(),
        		address.getNumber(),
        		address.getComplement(),
        		address.getCity(),
        		address.getState());
        
        return this;
    }
    public UserFactory createUserEmployee(String username, String password, String name) {
    	this.password = this.encodePassword(password);
    	
    	this.username = username;
    	this.name = name;
    	this.role = UserRole.EMPLOYEE;
    	
    	return this;
    }
    public UserFactory createUserAdmin(String username, String password, UserRole role) {
    	this.password = this.encodePassword(password);
    	this.username = username;
    	this.role = UserRole.ADMIN;
    	
    	return this;
    }
    
    public String formatPhoneNumber(String phoneNumber) {
    	PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
    	PhoneNumber phoneNumberObject = null;
    	
    	try {
    		phoneNumberObject = phoneNumberUtil.parse(phoneNumber, "BR");
			
		} catch (NumberParseException e) {
			e.printStackTrace();
			throw new RuntimeException("phoneNumber conversion failed");
		}
    	
        return phoneNumberUtil.format(phoneNumberObject, PhoneNumberFormat.INTERNATIONAL);
    }
    
    public String encodePassword(String password) {
    	return new BCryptPasswordEncoder().encode(password);
    }
    
    public User build() {
        return new User(id, username, password, name, email, phone_number, cpf, address, role);
    }
}