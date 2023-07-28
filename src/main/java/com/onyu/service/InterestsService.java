package com.onyu.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.onyu.exception.InvalidAccessException;
import com.onyu.model.dto.InterestsWrapper;
import com.onyu.model.entity.Interest;
import com.onyu.model.entity.User;
import com.onyu.repository.ChatRoomRepository;
import com.onyu.repository.InterestRepository;
import com.onyu.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterestsService {
	
	private final InterestRepository interestRepository;
	private final UserRepository userRepository;
	private final ChatRoomRepository chatRoomRepository;
	
	public List<InterestsWrapper> findAllInterestsService(String principal) throws InvalidAccessException {
		
		User user = userRepository.findByLoginId(principal)
		.orElseThrow(() -> new InvalidAccessException("다시 시도해 주세요"));;
		
		List<Interest> list = interestRepository.findAllByItrstUser(user);
		
		return list.stream().map(e -> {
		InterestsWrapper interestWrapper = new InterestsWrapper(e);
		long cnt = chatRoomRepository.countBypostChatId(e.getItrstPost());
		interestWrapper.getPost().setChatRoomCnt(cnt);
		return interestWrapper;
		}).toList();
	}
	
	
}
