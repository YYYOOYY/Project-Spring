package com.onyu.service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.onyu.exception.IdAlreadyExistsException;
import com.onyu.exception.InvalidAccessException;
import com.onyu.model.dto.UserWrapper;
import com.onyu.model.dto.request.JoinUserReqeust;
import com.onyu.model.dto.request.LoginRequest;
import com.onyu.model.dto.request.ProfileModifyRequest;
import com.onyu.model.dto.request.UserPassRequest;
import com.onyu.model.entity.ChatRoom;
import com.onyu.model.entity.Chatting;
import com.onyu.model.entity.EmailVerification;
import com.onyu.model.entity.Interest;
import com.onyu.model.entity.Post;
import com.onyu.model.entity.User;
import com.onyu.repository.ChatRoomRepository;
import com.onyu.repository.ChattingRepository;
import com.onyu.repository.EmailVerificationRepository;
import com.onyu.repository.InterestRepository;
import com.onyu.repository.PostPhotoRepository;
import com.onyu.repository.PostRepository;
import com.onyu.repository.UserRepository;

import jakarta.transaction.NotSupportedException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final PostPhotoRepository postPhotoRepository;
	private final InterestRepository interestRepository;
	private final ChatRoomRepository chatRoomRepository;
	private final ChattingRepository chattingRepository;
	private final EmailVerificationRepository emailVerificationRepository;

	@Value("${upload.server}")
	String uploadServer;

	@Value("${upload.basedir}")
	String baseDir;

	@Transactional
	public void joinUserService(JoinUserReqeust req) throws IdAlreadyExistsException, InvalidAccessException {

		if (userRepository.existsByLoginId(req.getLoginId())) {
			throw new IdAlreadyExistsException("이미 가입된 아이디입니다.");
		}

		EmailVerification found = emailVerificationRepository.findByEmail(req.getEmail())
				.orElseThrow(() -> new InvalidAccessException("잘 못된 접근입니다."));

		if (!found.isStatus()) {
			throw new InvalidAccessException("이메일이 인증되지 않았습니다.");
		}

		User user = new User();
		user.setLoginId(req.getLoginId());
		String hashedPw = BCrypt.hashpw(req.getLoginPassword(), BCrypt.gensalt());
		user.setLoginPassword(hashedPw);
		user.setNickname(req.getNickname());
		user.setEmail(found);
		user.setJoinde(new Date());

		var saved = userRepository.save(user);

		found.setUser(saved);

		emailVerificationRepository.save(found);
	}

	@Transactional
	public void checkPasswordService(LoginRequest req) throws InvalidAccessException {
		User found = userRepository.findByLoginId(req.getUsername())
				.orElseThrow(() -> new InvalidAccessException("아이디 혹은 비밀번호가 일치하지 않습니다."));
		if (!BCrypt.checkpw(req.getPassword(), found.getLoginPassword())) {
			throw new InvalidAccessException("아이디 혹은 비밀번호가 일치하지 않습니다.");
		}
	}

	public UserWrapper findByUserService(String principal) throws InvalidAccessException {

		User user = userRepository.findByLoginId(principal)
				.orElseThrow(() -> new InvalidAccessException("일시적인 오류가 발생했습니다 다시 시도해주세요."));

		return new UserWrapper(user);
	}

	@Transactional
	public void modifyProfileService(String principal, ProfileModifyRequest req)
			throws NotSupportedException, IllegalStateException, IOException, InvalidAccessException {

		User found = userRepository.findByLoginId(principal)
				.orElseThrow(() -> new InvalidAccessException("일시적인 오류가 발생했습니다 다시 시도해주세요."));

		if (req.getProfile() != null) {
			if (req.getCurrentPass() != null && req.getNewPass() != null) {
				if (BCrypt.checkpw(req.getCurrentPass(), found.getLoginPassword())) {
					String hashedPw = BCrypt.hashpw(req.getNewPass(), BCrypt.gensalt());
					found.setLoginPassword(hashedPw);
				}
			}
			MultipartFile multi = req.getProfile();

			if (!multi.getContentType().startsWith("image/")) {
				throw new NotSupportedException("이미지 파일만 설정가능합니다.");
			}

			File saveDir = new File(baseDir + "/profile/" + principal);
			saveDir.mkdirs();

			String filename = UUID.randomUUID()
					+ multi.getOriginalFilename().substring(multi.getOriginalFilename().lastIndexOf("."));
			File dest = new File(saveDir, filename);

			multi.transferTo(dest);

			found.setNickname(req.getNickname());
			found.setProfileImage(uploadServer + "/resource/profile/" + principal + "/" + filename);

			userRepository.save(found);
		} else {
			if (req.getCurrentPass() != null && req.getNewPass() != null) {
				if (BCrypt.checkpw(req.getCurrentPass(), found.getLoginPassword())) {
					String hashedPw = BCrypt.hashpw(req.getNewPass(), BCrypt.gensalt());
					found.setLoginPassword(hashedPw);
				}
			}
			found.setNickname(req.getNickname());

			userRepository.save(found);
		}
	}

	public boolean checkPasswordService(String principal, UserPassRequest req) throws InvalidAccessException {

		User found = userRepository.findByLoginId(principal)
				.orElseThrow(() -> new InvalidAccessException("잘 못된 접근입니다."));

		if (BCrypt.checkpw(req.getPass(), found.getLoginPassword())) {
			return true;
		} else {
			return false;
		}

	}

	@Transactional
	public void deleteUserService(String principal) throws InvalidAccessException {

		User found = userRepository.findByLoginId(principal)
				.orElseThrow(() -> new InvalidAccessException("잘 못된 접근입니다."));

		List<Post> postList = postRepository.findAllByWriter(found);

		User admin = userRepository.findByLoginId("admin0").orElse(null);

		for (Post one : postList) {
			if (!one.isStatus()) {
				List<ChatRoom> chatRoomList = chatRoomRepository.findAllByPostChatId(one);
				for (ChatRoom two : chatRoomList) {
					chattingRepository.deleteAllByChatRoom(two);
				}
				chatRoomRepository.deleteAllByPostChatId(one);

				interestRepository.deleteAllByItrstPost(one);

				postPhotoRepository.deleteAllByPost(one);

				postRepository.delete(one);
			} else {
				List<ChatRoom> chatRoomList = chatRoomRepository.findAllByPostChatId(one);
				for (ChatRoom two : chatRoomList) {
					List<Chatting> chattingList = chattingRepository.findAllByChatRoom(two);
					for (Chatting three : chattingList) {
						if (three.getChatWriter().equals(found)) {
							three.setChatWriter(admin);
							chattingRepository.save(three);
						}
					}

					two.setSeller(admin);
					chatRoomRepository.save(two);
				}
				one.setWriter(admin);
				postRepository.save(one);

				List<Interest> itrstList = interestRepository.findAllByItrstUser(found);
				for (Interest four : itrstList) {
					four.setItrstUser(admin);
					interestRepository.save(four);
				}
			}
		}

		emailVerificationRepository.deleteByUser(found);

		userRepository.delete(found);
	}

}
