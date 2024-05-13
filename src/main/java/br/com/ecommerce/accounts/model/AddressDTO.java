package br.com.ecommerce.accounts.model;

import jakarta.validation.constraints.NotNull;

public record AddressDTO (
		
    @NotNull
    String street,
    @NotNull
    String neighborhood,
    @NotNull
    String postal_code,
    @NotNull
    String number,
    @NotNull
    String complement,
    @NotNull
    String city,
    @NotNull
    String state
	){}