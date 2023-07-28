package com.onyu.model.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class CheckVerificationCodeRequest {
	
	@Email
	private String email;
	
	private String code;
}
