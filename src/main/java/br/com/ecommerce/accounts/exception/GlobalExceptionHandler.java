package br.com.ecommerce.accounts.exception;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	private final String CREDENTIALS_ERROR_MESSAGE = "Bad credentials";
	private final String ENTITY_NOT_FOUND_EXCEPTION = "User not found";
	private final String METHOD_ARGUMENT_NOT_VALID_MESSAGE = "Input validation error";
	private final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error";

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorMessage> handlerErro415(HttpMediaTypeNotSupportedException ex) {
        String unsupported = ex.getContentType() != null ? ex.getContentType().getType() + "/" + ex.getContentType().getSubtype() : "unknown";
        String supported = ex.getSupportedMediaTypes().stream()
                              .map(mediaType -> mediaType.getType() + "/" + mediaType.getSubtype())
                              .collect(Collectors.joining(", "));
        String message = String.format("Unsupported media type '%s'. Supported media types are: %s", unsupported, supported);

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()).body(new ErrorMessage(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), message));
    }
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorMessage> handleError400(HttpMessageNotReadableException ex) {
		return ResponseEntity.badRequest().body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ex.getMessage().split(":")[0]));
	}
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorMessageWithFields> handlerError400(MethodArgumentNotValidException ex) {
		var fields = ex.getFieldErrors().stream()
				.map(f -> new FieldErrorResponse(f.getField().toString(), f.getDefaultMessage()));
		
		return ResponseEntity
				.badRequest()
				.body(new ErrorMessageWithFields(
					HttpStatus.BAD_REQUEST.value(),
					METHOD_ARGUMENT_NOT_VALID_MESSAGE,
					fields));
	}
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorMessage> handlerErro400(IllegalArgumentException ex) {
		return ResponseEntity.badRequest().body(new ErrorMessage(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorMessage> handleError404(EntityNotFoundException ex) {
		return ResponseEntity
				.status(HttpStatus.UNAUTHORIZED)
				.body(new ErrorMessage(
					HttpStatus.NOT_FOUND.value(), 
					ENTITY_NOT_FOUND_EXCEPTION));
	}
	@ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleError404(NoResourceFoundException ex) {
    	return ResponseEntity.notFound().build();
    }
	
	@ExceptionHandler(FailedCredentialsException.class)
	public ResponseEntity<ErrorMessage> handleError401(FailedCredentialsException ex) {
		return ResponseEntity
				.status(HttpStatus.UNAUTHORIZED)
				.body(new ErrorMessage(
					HttpStatus.UNAUTHORIZED.value(), 
					CREDENTIALS_ERROR_MESSAGE));
	}
	
	@ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handlerError500(Exception ex) {
        return ResponseEntity
        		.internalServerError()
        		.body(new ErrorMessage(
        				HttpStatus.INTERNAL_SERVER_ERROR.value(),
        				INTERNAL_SERVER_ERROR_MESSAGE));
    }
	
	private record ErrorMessage(int status, Object error) {}
	private record ErrorMessageWithFields(int status, String error, Object fields) {}
	private record FieldErrorResponse(String field, String message) {}
}