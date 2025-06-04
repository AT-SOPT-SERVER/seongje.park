package org.sopt.service;

import static org.sopt.exception.ErrorCode.*;

import java.util.Optional;

import org.sopt.domain.User;
import org.sopt.dto.login.LoginRequest;
import org.sopt.dto.login.LoginResponse;
import org.sopt.dto.user.UserCreateRequest;
import org.sopt.exception.ErrorCode;
import org.sopt.exception.UserException;
import org.sopt.repository.user.UserRepository;
import org.sopt.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {

	private final UserRepository userRepository;
	private final JwtUtil jwtUtil;
	private final BCryptPasswordEncoder passwordEncoder;

	public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
		this.userRepository = userRepository;
		this.jwtUtil = jwtUtil;
		this.passwordEncoder = new BCryptPasswordEncoder();
	}

	// 로그인을 처리해보자.
	public LoginResponse login(LoginRequest loginRequest){
		// 일단, 이메일로 사용자 찾아야함.
		User user = userRepository.findByEmail(loginRequest.email())
			.orElseThrow(() -> new UserException(USER_NOT_FOUND));

		// 패스워드가 맞는지 검증한다.
		if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())){
			// 패스 워드 틀리면..
			throw new UserException(INVALID_PASSWORD);
		}

		// 패스 워드 일치시 JWT 토큰 생성
		String accessToken = jwtUtil.generateToken(user.getId(), user.getEmail());

		return LoginResponse.of(accessToken, user.getId(),
			user.getEmail(), user.getName());
	}

	// 회원가입을 처리해보자
	@Transactional
	public void join(UserCreateRequest userCreateRequest){

		// 이메일 중복검사
		if (userRepository.existsByEmail(userCreateRequest.email())){
			throw new UserException(DUPLICATE_EMAIL);
		}

		// 패스워드를 암호화해서 저장해야함.
		String encodedPassword = passwordEncoder.encode(userCreateRequest.password());

		// 사용자를 생성하고 db 에 저장합시다.
		User user = new User(userCreateRequest.name(), encodedPassword, userCreateRequest.email());

		userRepository.save(user);
	}

	public User getUserById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new UserException(USER_NOT_FOUND));
	}




}
