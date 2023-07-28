package com.onyu.model.dto.request;

import lombok.Data;

@Data
public class ChatRequest {
	private String message;
	private Long chatRoomId;
}
