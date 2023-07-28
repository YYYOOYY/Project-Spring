package com.onyu.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.onyu.exception.InvalidAccessException;
import com.onyu.exception.UnauthorizedAccessException;
import com.onyu.model.dto.ChatRoomWrapper;
import com.onyu.model.dto.ChattingWrapper;
import com.onyu.model.dto.UserWrapper;
import com.onyu.model.dto.request.ChatRequest;
import com.onyu.model.dto.request.PostIdRequest;
import com.onyu.model.entity.ChatRoom;
import com.onyu.model.entity.Chatting;
import com.onyu.model.entity.Post;
import com.onyu.model.entity.User;
import com.onyu.repository.ChatRoomRepository;
import com.onyu.repository.ChattingRepository;
import com.onyu.repository.PostRepository;
import com.onyu.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {
	private final ChatRoomRepository chatRoomRepository;
	private final ChattingRepository chattingRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	
	@Transactional
	public ChatRoomWrapper createChatRoomService(PostIdRequest req, String buyer) throws InvalidAccessException {
		
		Post found = postRepository.findById(req.getPostId())
				.orElseThrow(() -> new InvalidAccessException("일시적인 오류가 발생했습니다 다시 시도해주세요."));
		
		User user = userRepository.findByLoginId(buyer)
				.orElseThrow(() -> new InvalidAccessException("일시적인 오류가 발생했습니다 다시 시도해주세요."));
		
		if(found.getWriter().equals(user)) {
			throw new InvalidAccessException("자신과 채팅 불가");
		}
		
		ChatRoom saved;
		if(chatRoomRepository.existsBySellerAndBuyerAndPostChatId(found.getWriter(), user, found)) {
			saved = chatRoomRepository.findBySellerAndBuyerAndPostChatId(found.getWriter(), user, found);
		}else {
			ChatRoom one = new ChatRoom();
			one.setPostChatId(found);
			one.setSeller(found.getWriter());
			one.setBuyer(user);
			one.setCreated(new Date());
			
			saved = chatRoomRepository.save(one);
		}
		
		return new ChatRoomWrapper(saved);
	}
	
	@Transactional
	public void writeChatService(String principal, ChatRequest req) throws InvalidAccessException {
		
		User writer = userRepository.findByLoginId(principal)
				.orElseThrow(() -> new InvalidAccessException("일시적인 오류가 발생했습니다 다시 시도해주세요."));
		
		ChatRoom found = chatRoomRepository.findById(req.getChatRoomId())
				.orElseThrow(() -> new InvalidAccessException("일시적인 오류가 발생했습니다 다시 시도해주세요."));
		
		Chatting one = new Chatting();
		one.setChatRoom(found);
		one.setChatWriter(writer);
		one.setMessage(req.getMessage());
		one.setWrited(new Date());
		
		chattingRepository.save(one);
	}

	@Transactional
	public ChatRoomWrapper updateChatService(String principal, Long chatRoomId) throws InvalidAccessException, UnauthorizedAccessException {
		
		ChatRoom found = chatRoomRepository.findById(chatRoomId)
				.orElseThrow(() -> new InvalidAccessException("일시적인 오류가 발생했습니다 다시 시도해주세요."));
		
		User user = userRepository.findByLoginId(principal)
				.orElseThrow(() -> new InvalidAccessException("일시적인 오류가 발생했습니다 다시 시도해주세요."));
		
		List<Chatting> list = chattingRepository.findAllByChatRoom(found);
		for(Chatting one : list) {
			if(!one.getChatWriter().equals(user)) {
				one.setStatus(true);
				chattingRepository.save(one);				
			}
		}
		
		if(found.getBuyer().equals(user) || found.getSeller().equals(user)) {
			return new ChatRoomWrapper(found);
		}else {
			throw new UnauthorizedAccessException("잘못된 접근입니다.");			
		}
		
	}

	public List<ChatRoomWrapper> findAllChatRoomService(String principal) throws InvalidAccessException {
		
		User found = userRepository.findByLoginId(principal)
				.orElseThrow(() -> new InvalidAccessException("일시적인 오류가 발생했습니다 다시 시도해주세요."));
		
		List<ChatRoomWrapper> list = chatRoomRepository.findBySellerOrBuyerOrderByCreatedAsc(found, found)
				.stream().map(e -> new ChatRoomWrapper(e)).toList();
		
		
		for(ChatRoomWrapper one : list) {
			int cnt = 0;
			for(ChattingWrapper c : one.getChattings()) {
				if(!c.getChatWriter().equals(new UserWrapper(found)) && !c.isStatus()) {
					cnt += 1;
				}
			}
			one.setUnreadCnt(cnt);
		}
		
		return list;
	}

	public ChatRoomWrapper openChatRoomService(String principal, Long chatRoomId) throws InvalidAccessException, UnauthorizedAccessException {
		
		ChatRoom found = chatRoomRepository.findById(chatRoomId)
				.orElseThrow(() -> new InvalidAccessException("일시적인 오류가 발생했습니다 다시 시도해주세요."));
		
		User user = userRepository.findByLoginId(principal)
				.orElseThrow(() -> new InvalidAccessException("일시적인 오류가 발생했습니다 다시 시도해주세요."));
		
		List<Chatting> list = chattingRepository.findAllByChatRoom(found);
		for(Chatting one : list) {
			if(!one.getChatWriter().equals(user)) {
				one.setStatus(true);
				chattingRepository.save(one);				
			}
		}
		
		if(found.getBuyer().equals(user) || found.getSeller().equals(user)) {
			return new ChatRoomWrapper(found);
		}else {
			throw new UnauthorizedAccessException("잘못된 접근입니다.");	
		}		
		
	}
}
