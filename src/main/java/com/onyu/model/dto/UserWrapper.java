package com.onyu.model.dto;

import java.text.SimpleDateFormat;

import com.onyu.model.entity.User;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserWrapper {
	
	private Long id;
	private String LoginId;
	private String nickname;
	private String profileImage;
	private String email;
	private String joined;
	
	public UserWrapper(User entity) {
		this.id = entity.getId();
		this.LoginId = entity.getLoginId();
		this.nickname = entity.getNickname();
		this.profileImage = entity.getProfileImage();
		this.email = entity.getEmail().getEmail();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String format = sdf.format(entity.getJoinde());
		this.joined = format;
	}
}
