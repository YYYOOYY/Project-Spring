package com.onyu.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.onyu.exception.IdAlreadyExistsException;
import com.onyu.exception.InvalidAccessException;
import com.onyu.exception.NotFoundException;
import com.onyu.exception.UnauthorizedAccessException;
import com.onyu.model.dto.request.CheckVerificationCodeRequest;
import com.onyu.model.dto.request.EmailRequest;
import com.onyu.model.entity.EmailVerification;
import com.onyu.repository.EmailVerificationRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {
	
	private final JavaMailSender mailsender;
	private final EmailVerificationRepository emailVerificationRepository;
	
	@Transactional
	public void sendVerificationCode(EmailRequest req) throws InvalidAccessException, IdAlreadyExistsException, NotFoundException {
		
		Optional<EmailVerification> found = emailVerificationRepository.findByEmail(req.getEmail());
		
		int secretNum = (int)(Math.random()*1000000);
		String code = String.format("%06d", secretNum);
		
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(req.getEmail());
		message.setFrom("dideoduf1@gmail.com");
		message.setSubject("[중고마켓] 인증코드");
		message.setText("""
				안녕하세요 중고마켓에서 인증번호를 보내드립니다.
				
				화면에 인증코드를 입력해주세요
				
				인증코드 : #{code}
				""".replace("#{code}", code));
		
		// 이메일인증을 한번도 안 받은 상태
		if(found.isEmpty()) {
			mailsender.send(message);
			
			EmailVerification one = new EmailVerification();
			one.setCode(code);
			one.setEmail(req.getEmail());
			one.setCreated(new Date());
			
			emailVerificationRepository.save(one);
		}
		
		// 이메일인증을 끝낸 상태
		if(found.isPresent() && found.get().isStatus() && found.get().getUser() != null) {
			throw new IdAlreadyExistsException("이미 사용중인 이메일입니다.");
		}
		
		// 이메일인증코드를 발급했지만 인증을 하지 않았거나 실패한 상태 혹은 이메일인증은 성공했지만 아이디를 생성 안했을 때
		if((found.isPresent() && !found.get().isStatus()) || 
				(found.isPresent() && found.get().isStatus() && found.get().getUser() == null)) {
			mailsender.send(message);
			
			EmailVerification result = emailVerificationRepository.findByEmail(req.getEmail())
					.orElseThrow(() -> new NotFoundException("이메일 인증을 다시 진행해주세요"));
			
			if(result.isStatus()) {
				result.setStatus(false);
			}
			result.setCount(0);
			result.setCode(code);
			result.setCreated(new Date());
			
			emailVerificationRepository.save(result);
		}
		
	}
	
	public void checkVerificationCode(CheckVerificationCodeRequest req) throws InvalidAccessException, NotFoundException, UnauthorizedAccessException {
		
		EmailVerification found = emailVerificationRepository.findByEmail(req.getEmail())
				.orElseThrow(() -> new InvalidAccessException("이메일 인증을 다시 진행해주세요"));
		
		if(found.getCount() >= 3) {
			found.setCount(0);
			emailVerificationRepository.save(found);
			throw new NotFoundException("이메일 인증코드를 다시 받아주세요");
		}
		
		if(found.getCode().equals(req.getCode())) {
			found.setStatus(true);;
			emailVerificationRepository.save(found);
		}else {
			found.setCount(found.getCount() + 1);
			emailVerificationRepository.save(found);
			throw new UnauthorizedAccessException("인증코드를 다시 입력해주세요.");
		}
	}
}
