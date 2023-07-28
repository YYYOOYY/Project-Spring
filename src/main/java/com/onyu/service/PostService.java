package com.onyu.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.onyu.exception.InvalidAccessException;
import com.onyu.exception.UnauthorizedAccessException;
import com.onyu.model.dto.PostWrapper;
import com.onyu.model.dto.request.PostRequest;
import com.onyu.model.dto.request.PostIdRequest;
import com.onyu.model.dto.request.TokenRequest;
import com.onyu.model.dto.response.OnePostResponse;
import com.onyu.model.entity.ChatRoom;
import com.onyu.model.entity.Interest;
import com.onyu.model.entity.Post;
import com.onyu.model.entity.PostPhoto;
import com.onyu.model.entity.User;
import com.onyu.repository.ChatRoomRepository;
import com.onyu.repository.ChattingRepository;
import com.onyu.repository.InterestRepository;
import com.onyu.repository.PostPhotoRepository;
import com.onyu.repository.PostRepository;
import com.onyu.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final PostPhotoRepository postPhotoRepository;
	private final JWTservice jwTservice;
	private final InterestRepository interestRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final ChattingRepository chattingRepository;

	@Value("${upload.basedir}")
	private String uploadBaseDir;

	@Value("${upload.server}")
	private String uploadServer;

	@Transactional
	public void create(String writer, PostRequest req)
			throws InvalidAccessException, IllegalStateException, IOException {

		if (req.getCate().equals("") || req.getCity().equals("") || req.getTitle().equals("")) {
			throw new InvalidAccessException("최소한의 정보를 입력해야합니다.");
		}

		User user = userRepository.findByLoginId(writer).orElseThrow(() -> new InvalidAccessException("잘 못된 접근입니다."));

		Post post = new Post();
		post.setTitle(req.getTitle());
		post.setContent(req.getContent());
		post.setPrice(req.getPrice());
		post.setCate(req.getCate());
		post.setWriter(user);
		post.setWrited(new Date());
		post.setViewCount(0L);
		post.setInterestedCount(0L);
		post.setCity(req.getCity());

		var saved = postRepository.save(post);

		if (req.getPhotos() != null) {
			File uploadDirectory = new File(uploadBaseDir + "/photo/" + saved.getId());
			uploadDirectory.mkdirs();

			for (MultipartFile multi : req.getPhotos()) {
				String fileName = String.valueOf(UUID.randomUUID());
				String extension = multi.getOriginalFilename().split("\\.")[1];
				File dest = new File(uploadDirectory, fileName + "." + extension);

				multi.transferTo(dest);

				PostPhoto photos = new PostPhoto();

				photos.setType(multi.getContentType());
				photos.setImageUrl(
						uploadServer + "/resource/photo/" + saved.getId() + "/" + fileName + "." + extension);
				photos.setPost(saved);
				postPhotoRepository.save(photos);
			}
		}
	}

	@Transactional
	public void update(String writer, PostRequest req, Long postId)
			throws InvalidAccessException, IllegalStateException, IOException {

		if (req.getCate().equals("") || req.getCity().equals("") || req.getTitle().equals("")) {
			throw new InvalidAccessException("최소한의 정보를 입력해야합니다.");
		}

		User user = userRepository.findByLoginId(writer).orElseThrow(() -> new InvalidAccessException("잘 못된 접근입니다."));

		Post post = postRepository.findById(postId).orElseThrow(() -> new InvalidAccessException("다시 시도해 주세요"));

		post.setTitle(req.getTitle());
		post.setContent(req.getContent());
		post.setPrice(req.getPrice());
		post.setCate(req.getCate());
		post.setCity(req.getCity());

		var saved = postRepository.save(post);

		if (req.getPhotos() != null) {

			postPhotoRepository.deleteAllByPost(post);

			File uploadDirectory = new File(uploadBaseDir + "/photo/" + saved.getId());
			uploadDirectory.mkdirs();

			for (MultipartFile multi : req.getPhotos()) {
				String fileName = String.valueOf(UUID.randomUUID());
				String extension = multi.getOriginalFilename().split("\\.")[1];
				File dest = new File(uploadDirectory, fileName + "." + extension);

				multi.transferTo(dest);

				PostPhoto photos = new PostPhoto();

				photos.setType(multi.getContentType());
				photos.setImageUrl(
						uploadServer + "/resource/photo/" + saved.getId() + "/" + fileName + "." + extension);
				photos.setPost(saved);
				postPhotoRepository.save(photos);
			}
		}
	}

	public Long PostSize() {
		return postRepository.count();
	}

	public List<PostWrapper> allPosts(int page, String keyword) {
		Sort sort = Sort.by(Sort.Order.desc("writed"), Sort.Order.asc("status"));

		List<Post> entityList = new ArrayList<>();
		if (keyword == null || keyword.equals("")) {
			entityList = postRepository.findAll(PageRequest.of(page - 1, 12, sort)).toList();
		} else {
			entityList = postRepository.findAllByTitleContainingIgnoreCase(keyword, PageRequest.of(page - 1, 12, sort))
					.toList();

		}

		return entityList.stream().map(e -> {
			long cnt = chatRoomRepository.countBypostChatId(e);
			PostWrapper postWrapper = new PostWrapper(e);
			postWrapper.setChatRoomCnt(cnt);
			return postWrapper;
		}).toList();
	}

	@Transactional
	public OnePostResponse OnePost(Long postId, String loginId) throws InvalidAccessException {
		Post foundPost = postRepository.findById(postId).orElseThrow(() -> new InvalidAccessException("다시 시도해 주세요"));

		User foundUser = userRepository.findByLoginId(loginId).orElse(null);

		foundPost.setViewCount(foundPost.getViewCount() + 1);

		Post saved = postRepository.save(foundPost);

		long cnt = chatRoomRepository.countBypostChatId(foundPost);

		PostWrapper wrapper = new PostWrapper(saved);

		wrapper.setChatRoomCnt(cnt);

		boolean interested;

		if (foundUser == null) {
			interested = false;
		} else {
			interested = interestRepository.existsByItrstPostAndItrstUser(foundPost, foundUser);
		}

		return new OnePostResponse(wrapper, interested);
	}

	@Transactional
	public void updateInterestedCount(PostIdRequest pReq, TokenRequest tReq) throws InvalidAccessException {

		Post foundPost = postRepository.findById(pReq.getPostId())
				.orElseThrow(() -> new InvalidAccessException("다시 시도해 주세요"));

		String loginId = jwTservice.verifyToken(tReq.getToken());

		User foundUser = userRepository.findByLoginId(loginId)
				.orElseThrow(() -> new InvalidAccessException("다시 시도해 주세요"));

		if (foundPost.getWriter().getId().equals(foundUser.getId())
				|| foundPost.getWriter().getLoginId().equals(foundUser.getLoginId())) {
			throw new InvalidAccessException("자신의 게시물은 관심목록에 추가가 불가능합니다.");
		}

		if (interestRepository.existsByItrstPostAndItrstUser(foundPost, foundUser)) {

			interestRepository.deleteByItrstPostAndItrstUser(foundPost, foundUser);

		} else {
			Interest one = new Interest();

			one.setItrstPost(foundPost);
			one.setItrstUser(foundUser);

			interestRepository.save(one);
		}

		long count = interestRepository.countByItrstPost(foundPost);

		foundPost.setInterestedCount(count);

		postRepository.save(foundPost);
	}

	@Transactional
	public boolean existInterested(PostIdRequest pReq, TokenRequest tReq) throws InvalidAccessException {

		Post foundPost = postRepository.findById(pReq.getPostId())
				.orElseThrow(() -> new InvalidAccessException("다시 시도해 주세요"));

		String loginId = jwTservice.verifyToken(tReq.getToken());

		User foundUser = userRepository.findByLoginId(loginId)
				.orElseThrow(() -> new InvalidAccessException("다시 시도해 주세요"));

		return interestRepository.existsByItrstPostAndItrstUser(foundPost, foundUser);
	}

	public long importInterestedCount(PostIdRequest req) throws InvalidAccessException {

		Post found = postRepository.findById(req.getPostId())
				.orElseThrow(() -> new InvalidAccessException("다시 시도해주세요"));

		return interestRepository.countByItrstPost(found);

	}

	public void updateStatus(String principal, PostIdRequest req)
			throws InvalidAccessException, UnauthorizedAccessException {

		Post found = postRepository.findById(req.getPostId())
				.orElseThrow(() -> new InvalidAccessException("다시 시도해 주세요"));

		if (!found.getWriter().getLoginId().equals(principal)) {
			throw new UnauthorizedAccessException("잘 못된 접근입니다.");
		}

		found.setStatus(true);
		postRepository.save(found);
	}

	@Transactional
	public void deleteByPostService(String principal, PostIdRequest req)
			throws InvalidAccessException, UnauthorizedAccessException {

		Post result = postRepository.findById(req.getPostId())
				.orElseThrow(() -> new InvalidAccessException("다시 시도해 주세요."));

		if (principal.equals("admin0")) {
			List<ChatRoom> chatRoomList = chatRoomRepository.findAllByPostChatId(result);

			for (ChatRoom one : chatRoomList) {
				chattingRepository.deleteAllByChatRoom(one);
			}

			chatRoomRepository.deleteAllByPostChatId(result);

			interestRepository.deleteAllByItrstPost(result);

			postPhotoRepository.deleteAllByPost(result);

			postRepository.delete(result);
		} else {

			if (result.isStatus()) {
				throw new UnauthorizedAccessException("판매완료된 상품은 삭제가 불가능합니다.");
			}

			if (!principal.equals(result.getWriter().getLoginId())) {
				throw new UnauthorizedAccessException("잘 못된 접근입니다.");
			}

			List<ChatRoom> chatRoomList = chatRoomRepository.findAllByPostChatId(result);

			for (ChatRoom one : chatRoomList) {
				chattingRepository.deleteAllByChatRoom(one);
			}

			chatRoomRepository.deleteAllByPostChatId(result);

			interestRepository.deleteAllByItrstPost(result);

			postPhotoRepository.deleteAllByPost(result);

			postRepository.delete(result);
		}
	}

	public List<PostWrapper> findMyPostsService(String principal) throws InvalidAccessException {

		User foundUser = userRepository.findByLoginId(principal)
				.orElseThrow(() -> new InvalidAccessException("다시 시도해 주세요"));

		List<Post> foundPost = postRepository.findAllByWriter(foundUser);
		
		return foundPost.stream().map(e -> {
			long cnt = chatRoomRepository.countBypostChatId(e);
			PostWrapper postWrapper = new PostWrapper(e);
			postWrapper.setChatRoomCnt(cnt);
			return postWrapper;
		}).toList();
	}

}