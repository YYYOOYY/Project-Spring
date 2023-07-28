package com.onyu.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InvalidAccessException extends Exception {
	
	public InvalidAccessException(String message) {
		super(message);
	}
	
}
