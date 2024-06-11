package br.com.ecommerce.accounts.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneNumberValidatorImpl.class)
public @interface PhoneNumberValidator {
	
    String message() default "Invalid phone number.";
	String countryCode() default "BR";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}