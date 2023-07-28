package com.onyu.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class IdAlreadyExistsException extends Exception {
	
	public IdAlreadyExistsException(String message) {
		super(message);
	}
}
