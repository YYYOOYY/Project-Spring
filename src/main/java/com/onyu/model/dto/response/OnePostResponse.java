package com.onyu.model.dto.response;

import com.onyu.model.dto.PostWrapper;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OnePostResponse {
	private PostWrapper post;
	private boolean interested;
}
