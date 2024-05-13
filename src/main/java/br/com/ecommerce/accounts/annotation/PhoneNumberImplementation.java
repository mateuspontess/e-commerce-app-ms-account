package br.com.ecommerce.accounts.annotation;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberImplementation implements ConstraintValidator<PhoneNumberValidator, String>{
	
	@Override
	public void initialize(PhoneNumberValidator constraintAnnotation) {}
	
	@Override
	public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
		PhoneNumberUtil pnUtil = PhoneNumberUtil.getInstance();
		PhoneNumber pn;
		
		try {
			pn = pnUtil.parse(phoneNumber, "BR");
			
		} catch (NumberParseException e) {
			return false;
		}
		
		return pnUtil.isValidNumberForRegion(pn, "BR");
	}
}