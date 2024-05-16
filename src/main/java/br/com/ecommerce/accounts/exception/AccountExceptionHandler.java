package br.com.ecommerce.accounts.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class AccountExceptionHandler {
	
	private final String CREDENTIALS_ERROR_MESSAGE = "Bad credentials";
	private final String METHOD_ARGUMENT_NOT_VALID_MESSAGE = "Input validation error";
	private final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error";
	
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handlerException(Exception ex) {
        return ResponseEntity
        		.internalServerError()
        		.body(new ErrorMessage(
        				HttpStatus.INTERNAL_SERVER_ERROR.value(),
        				INTERNAL_SERVER_ERROR_MESSAGE));
    }
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorMessageWithFields> handlerMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		var fields = ex.getFieldErrors().stream()
				.map(f -> new FieldErrorResponse(f.getField().toString(), f.getDefaultMessage()));
		var response = new ErrorMessageWithFields(
				HttpStatus.BAD_REQUEST.value(),
				METHOD_ARGUMENT_NOT_VALID_MESSAGE,
				fields);
		
		return ResponseEntity
				.badRequest()
				.body(response);
	}
	
	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorMessage> handlerEntityNotFoundException(EntityNotFoundException ex) {
		var response = new ErrorMessage(
				HttpStatus.UNAUTHORIZED.value(), 
				CREDENTIALS_ERROR_MESSAGE);
		
		return ResponseEntity
				.status(HttpStatus.UNAUTHORIZED)
				.body(response);
	}
	
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handlerHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return ResponseEntity
        		.badRequest()
        		.body(ex.getMessage());
    }
	
	@ExceptionHandler(FailedCredentialsException.class)
	public ResponseEntity<ErrorMessage> handlerFailedCredentialsException(FailedCredentialsException ex) {
		var response = new ErrorMessage(
				HttpStatus.UNAUTHORIZED.value(), 
				CREDENTIALS_ERROR_MESSAGE);
		
		return ResponseEntity
				.status(HttpStatus.UNAUTHORIZED)
				.body(response);
	}
	
	private record ErrorMessage(int status, Object error) {}
	private record ErrorMessageWithFields(int status, String error, Object fields) {}
	private record FieldErrorResponse(String field, String message) {}
}