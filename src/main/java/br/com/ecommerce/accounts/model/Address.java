package br.com.ecommerce.accounts.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@Embeddable
public class Address {
	
	private String street;
	private String neighborhood;
	private String postal_code;
	private String number;
	private String complement;
	private String city;
	private String state;
}