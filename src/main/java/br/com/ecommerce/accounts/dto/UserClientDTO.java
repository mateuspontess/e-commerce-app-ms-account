package br.com.ecommerce.accounts.dto;

import org.hibernate.validator.constraints.br.CPF;

import br.com.ecommerce.accounts.annotation.PhoneNumberValidator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserClientDTO (
		
    @Size(min = 3, message = "size must be 3 or more characters")
    String username,
    
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
        message = "The password must contain at least one letter, one special character and be at least 8 characters long")
    String password,
    
    @NotBlank
    String name,
    
    @Email
    String email,
    
    @PhoneNumberValidator
    String phone_number,
    
    @CPF
    String cpf,
    
    @NotNull @Valid
    AddressDTO address
	) {}