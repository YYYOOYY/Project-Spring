package com.onyu.model.dto.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class ProfileModifyRequest {
	private String nickname;
	private MultipartFile profile;
	
	private String currentPass;
	private String newPass;
}
