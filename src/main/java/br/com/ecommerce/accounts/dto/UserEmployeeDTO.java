package br.com.ecommerce.accounts.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserEmployeeDTO (
	
    @Size(min = 3, message = "size must be 3 or more characters")
    String username,
    
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
        message = "The password must contain at least one letter, one special character and be at least 8 characters long")
    String password,
    
    @NotBlank
    String name
	) {}