package com.onyu.model.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class EmailRequest {
	@Email
	private String email;
}
