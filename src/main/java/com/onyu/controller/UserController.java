package com.onyu.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.onyu.exception.IdAlreadyExistsException;
import com.onyu.exception.InvalidAccessException;
import com.onyu.exception.NotFoundException;
import com.onyu.exception.UnauthorizedAccessException;
import com.onyu.model.dto.UserWrapper;
import com.onyu.model.dto.request.CheckVerificationCodeRequest;
import com.onyu.model.dto.request.EmailRequest;
import com.onyu.model.dto.request.JoinUserReqeust;
import com.onyu.model.dto.request.LoginRequest;
import com.onyu.model.dto.request.ProfileModifyRequest;
import com.onyu.model.dto.request.UserPassRequest;
import com.onyu.model.dto.response.ValidateUserResponse;
import com.onyu.service.JWTservice;
import com.onyu.service.MailService;
import com.onyu.service.UserService;

import jakarta.transaction.NotSupportedException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
@CrossOrigin
public class UserController {

	private final UserService userService;
	private final MailService mailService;
	private final JWTservice jwTservice;
	
	// 회원가입
	@PostMapping("/join")
	public ResponseEntity<Void> joinUserHandle(@Valid JoinUserReqeust req) throws IdAlreadyExistsException, InvalidAccessException {
		
		userService.joinUserService(req);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	// 이메일코드발급
	@PostMapping("/verify-code")
	public ResponseEntity<Void> sendEmailVerificationHandle(EmailRequest req) throws InvalidAccessException, IdAlreadyExistsException, NotFoundException {
		
		mailService.sendVerificationCode(req);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	// 이메일코드인증
	@PatchMapping("/verify-code")
	public ResponseEntity<Void> checkEmailVerificationhandle(CheckVerificationCodeRequest req) throws InvalidAccessException, NotFoundException, UnauthorizedAccessException {
		
		mailService.checkVerificationCode(req);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping("/validate")
	public ResponseEntity<ValidateUserResponse> validateHandle(LoginRequest req) throws InvalidAccessException{
		
		userService.checkPasswordService(req);
		
		String token = jwTservice.createToken(req.getUsername());
		
		log.info("토큰 = " + token);
		
		ValidateUserResponse response = new ValidateUserResponse(200, token, req.getUsername());
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/profile")
	public ResponseEntity<?> profileHandle(@AuthenticationPrincipal String principal) throws InvalidAccessException {
		
		UserWrapper result = userService.findByUserService(principal);
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
	
	@PostMapping("/profile/modify")
	public ResponseEntity<?> modifyProfileHandle(@AuthenticationPrincipal String principal, ProfileModifyRequest req) throws IllegalStateException, NotSupportedException, IOException, InvalidAccessException {
		
		userService.modifyProfileService(principal, req);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping("/checkPass")
	public ResponseEntity<?> checkPasswordHandle(@AuthenticationPrincipal String principal, UserPassRequest req) throws InvalidAccessException {
		
		boolean rst = userService.checkPasswordService(principal, req);
		
		return new ResponseEntity<>(rst, HttpStatus.OK);
	}
	
	@DeleteMapping("/delete")
	public ResponseEntity<?> deleteUserHandle(@AuthenticationPrincipal String principal) throws InvalidAccessException {
		userService.deleteUserService(principal);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
