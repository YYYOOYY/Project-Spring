package com.onyu.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class JoinUserReqeust {
	
	@Pattern(regexp = "^[a-z][a-z0-9]{4,11}$", message = "loginId는 5자 이상 12자 이하의 영어 소문자와 숫자 조합이어야 합니다.")
	private String loginId;
	@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$", message = "loginPassword는 8자 이상의 영어, 숫자, 특수문자 조합이어야 합니다.")
	private String loginPassword;
	@Pattern(regexp = "^[a-zA-Z가-힣]{2,8}$", message = "nickname은 2자 이상 8자 이하의 영어와 한글만 가능합니다.")
	private String nickname;
	@Email(message = "올바른 이메일 형식이어야 합니다.")
	private String email;
}
