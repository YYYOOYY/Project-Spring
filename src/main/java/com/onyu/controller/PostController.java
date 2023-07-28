package com.onyu.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.onyu.exception.InvalidAccessException;
import com.onyu.exception.UnauthorizedAccessException;
import com.onyu.model.dto.PostWrapper;
import com.onyu.model.dto.request.PostRequest;
import com.onyu.model.dto.request.PostIdRequest;
import com.onyu.model.dto.request.TokenRequest;
import com.onyu.model.dto.response.OnePostResponse;
import com.onyu.model.dto.response.PostListResponse;
import com.onyu.service.PostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
@CrossOrigin
public class PostController {

	private final PostService postService;

	@PostMapping("/create")
	public ResponseEntity<?> createPostHandle(@AuthenticationPrincipal String principal, PostRequest req)
			throws IllegalStateException, InvalidAccessException, IOException {
		postService.create(principal, req);

		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PatchMapping("/update/{id}")
	public ResponseEntity<?> updatePostHandle(@AuthenticationPrincipal String principal, PostRequest req,
			@PathVariable("id") Long postId) throws IllegalStateException, InvalidAccessException, IOException {
		postService.update(principal, req, postId);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/list")
	public ResponseEntity<?> findAllPostHandle(@RequestParam(defaultValue = "1") int page, String q) {

		Long total = postService.PostSize();
		List<PostWrapper> posts = postService.allPosts(page, q);
		PostListResponse response = new PostListResponse(total, posts);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/detail/{id}")
	public ResponseEntity<?> findOnePostHandle(@PathVariable("id") Long postId,
			@AuthenticationPrincipal String principal) throws InvalidAccessException {

		OnePostResponse req = postService.OnePost(postId, principal);

		return new ResponseEntity<>(req, HttpStatus.OK);
	}

	@PostMapping("/interested")
	public ResponseEntity<?> updateInterestedCountHandle(PostIdRequest pReq, TokenRequest tReq)
			throws InvalidAccessException {

		postService.updateInterestedCount(pReq, tReq);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/interested/exist")
	public ResponseEntity<?> existInterestedHandle(PostIdRequest pReq, TokenRequest tReq)
			throws InvalidAccessException {

		boolean rst = postService.existInterested(pReq, tReq);

		return new ResponseEntity<>(rst, HttpStatus.OK);
	}

	@GetMapping("/interested/import")
	public ResponseEntity<?> importInterestedCountHandle(PostIdRequest req) throws InvalidAccessException {

		long count = postService.importInterestedCount(req);

		return new ResponseEntity<>(count, HttpStatus.OK);
	}

	@PatchMapping("/status")
	public ResponseEntity<?> updateStatusHandle(@AuthenticationPrincipal String principal, PostIdRequest req)
			throws InvalidAccessException, UnauthorizedAccessException {
		postService.updateStatus(principal, req);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping("/delete")
	public ResponseEntity<?> deleteByPostHandle(@AuthenticationPrincipal String principal, PostIdRequest req)
			throws InvalidAccessException, UnauthorizedAccessException {

		postService.deleteByPostService(principal, req);

		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping("/myposts")
	public ResponseEntity<?> findMyPostsHandle(@AuthenticationPrincipal String principal) throws InvalidAccessException {
		
		Long total = postService.PostSize();
		List<PostWrapper> posts = postService.findMyPostsService(principal);
		PostListResponse response = new PostListResponse(total, posts);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
