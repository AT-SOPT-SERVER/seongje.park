package org.sopt.controller;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.sopt.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
public class AuthController {

	// base authorization
	@GetMapping("/login")
	public ResponseEntity<String> login(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		// authHeader 가 null 이거나 Basic 으로 시작하지 않으면
		if (authHeader == null || !authHeader.startsWith("Basic ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body("헤더가 이상합니다");
		}

		// basic 만 빼고 가져옴.
		String decodedString = authHeader.substring("Basic ".length());
		byte[] decodedByte = Base64.getDecoder().decode(decodedString);
		String credentials = new String(decodedByte, StandardCharsets.UTF_8);

		String[] parts = credentials.split(":");
		String username = parts[0];
		String password = parts[1];

		// 간단한 검증
		if (username.equals("soptUser") && password.equals("sopt1234")) {
			return ResponseEntity.ok("인증 성공");
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 실패");
		}
	}

	// 쿠키 실습
	@GetMapping("/set-cookie")
	public ResponseEntity<String> setCookie(HttpServletRequest request,
		HttpServletResponse response) {

		String username = "userSopt";
		String password = "sopt1234";

		Cookie userNameCookie = new Cookie("userId", username);
		Cookie passwordCookie = new Cookie("password", password);

		userNameCookie.setPath("/");
		passwordCookie.setPath("/");

		response.addCookie(userNameCookie);
		response.addCookie(passwordCookie);

		return ResponseEntity.ok("쿠키를 잘 구웠습니다");

	}

	@GetMapping("/get-cookie")
	public ResponseEntity<String> getCookie(
		@CookieValue("userId") String userId,
		@CookieValue("password") String password
	) {
		return ResponseEntity.ok("받은 쿠키" +
			"유저 아이디 : " + userId + "유저 비밀번호"
			+ password);

	}

	// 세션 실습
	@PostMapping("/login")
	public ResponseEntity<String> login2(HttpServletRequest request) {

		String userId = "userSopt";
		String password = "sopt1234";

		// userId 와 password 에 대한 검증을 해야한다.
		if (userId.equals("userSpot") && password.equals("sopt1234")) {
			// 유효한 id와 password 이면, 세션을 생성한다.
			HttpSession session = request.getSession(true);
			session.setAttribute("user", new User("soptUser", "메일"));
			return ResponseEntity.ok("세션 저장 완료");

		}

		throw new RuntimeException("");

	}
}
