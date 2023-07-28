package com.onyu.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.onyu.exception.InvalidAccessException;
import com.onyu.exception.UnauthorizedAccessException;
import com.onyu.model.dto.ChatRoomWrapper;
import com.onyu.model.dto.ChattingWrapper;
import com.onyu.model.dto.request.ChatRequest;
import com.onyu.model.dto.request.PostIdRequest;
import com.onyu.service.ChatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@CrossOrigin
public class ChatController {
	
	private final ChatService chatService;
	
	// 채팅방 생성 및 구매자 첫 채팅방입장하는 작업
	@PostMapping("/create")
	public ResponseEntity<?> createChatRoomHandle(PostIdRequest req, @AuthenticationPrincipal String principal) throws InvalidAccessException {
		
		ChatRoomWrapper resp = chatService.createChatRoomService(req, principal);
		
		return new ResponseEntity<>(resp, HttpStatus.OK);
	}
	
	// 채팅보내는 작업
	@PostMapping("/write")
	public ResponseEntity<?> writeChatHandle(@AuthenticationPrincipal String principal, ChatRequest req) throws InvalidAccessException {
		System.out.println(req.getMessage() + "-----" + req.getChatRoomId());
		chatService.writeChatService(principal, req);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	// 채팅업데이트 하는 작업
	@GetMapping("/update/{id}")
	public ResponseEntity<?> updateChatHandle(@AuthenticationPrincipal String principal, @PathVariable("id") Long chatRoomId) throws InvalidAccessException, UnauthorizedAccessException {
		
		ChatRoomWrapper chatList = chatService.updateChatService(principal, chatRoomId);
		
		return new ResponseEntity<>(chatList, HttpStatus.OK);
	}
	
	// 채팅방리스트를 찾는 작업
	@GetMapping("/findAllChatRoom")
	public ResponseEntity<?> findAllChatRoomHandle(@AuthenticationPrincipal String principal) throws InvalidAccessException {
		
		List<ChatRoomWrapper> roomList = chatService.findAllChatRoomService(principal);
		
		return new ResponseEntity<>(roomList, HttpStatus.OK);
	}
	
	// 특정 채팅방 들어가는 작업
	@GetMapping("/openChatRoom/{id}")
	public ResponseEntity<?> openChatRoomHandle(@AuthenticationPrincipal String principal, @PathVariable("id") Long chatRoomId) throws InvalidAccessException, UnauthorizedAccessException{
		
		ChatRoomWrapper result = chatService.openChatRoomService(principal, chatRoomId);
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
}
