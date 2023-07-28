package com.onyu.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.onyu.exception.IdAlreadyExistsException;
import com.onyu.exception.InvalidAccessException;
import com.onyu.exception.NotFoundException;
import com.onyu.exception.UnauthorizedAccessException;
import com.onyu.model.dto.response.ErrorResponse;

@ControllerAdvice
public class ExceptionHandlerConfiguration {
	
	@ExceptionHandler(IdAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> exceptionHandle(IdAlreadyExistsException ex) {
		ErrorResponse response = new ErrorResponse(409, ex.getMessage(), System.currentTimeMillis());
		
		return new ResponseEntity<>(response, HttpStatus.CONFLICT);
	}
	
	@ExceptionHandler(InvalidAccessException.class)
	public ResponseEntity<ErrorResponse> exceptionHandle(InvalidAccessException ex) {
		ErrorResponse response = new ErrorResponse(400, ex.getMessage(), System.currentTimeMillis());
		
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(UnauthorizedAccessException.class)
	public ResponseEntity<ErrorResponse> exceptionHandle(UnauthorizedAccessException ex) {
		ErrorResponse response = new ErrorResponse(403, ex.getMessage(), System.currentTimeMillis());
		
		return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
	}
	
	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ErrorResponse> exceptionHandle(NotFoundException ex) {
		ErrorResponse response = new ErrorResponse(404, ex.getMessage(), System.currentTimeMillis());
		
		return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
	}
	
}
