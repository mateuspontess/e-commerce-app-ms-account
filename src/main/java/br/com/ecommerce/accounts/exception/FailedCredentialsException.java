package br.com.ecommerce.accounts.exception;

public class FailedCredentialsException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public FailedCredentialsException(String message) {
		super(message);
	}
}