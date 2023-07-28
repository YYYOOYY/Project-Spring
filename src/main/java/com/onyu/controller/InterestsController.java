package com.onyu.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.onyu.exception.InvalidAccessException;
import com.onyu.model.dto.InterestsWrapper;
import com.onyu.service.InterestsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/interests")
@RequiredArgsConstructor
@CrossOrigin
public class InterestsController {

	private final InterestsService interestsService;

	@GetMapping("/findAll")
	public ResponseEntity<?> findAllInterestsHandle(@AuthenticationPrincipal String principal)
			throws InvalidAccessException {

		List<InterestsWrapper> list = interestsService.findAllInterestsService(principal);

		return new ResponseEntity<>(list, HttpStatus.OK);
	}
}
