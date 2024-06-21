package br.com.ecommerce.accounts.dto;

import jakarta.validation.constraints.NotBlank;

public record AddressDTO (
		
    @NotBlank
    String street,
    @NotBlank
    String neighborhood,
    @NotBlank
    String postal_code,
    @NotBlank
    String number,
    @NotBlank
    String complement,
    @NotBlank
    String city,
    @NotBlank
    String state
    ) {}