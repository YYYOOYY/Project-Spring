package com.onyu.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UnauthorizedAccessException extends Exception {

	public UnauthorizedAccessException(String message) {
		super(message);
	}
}
