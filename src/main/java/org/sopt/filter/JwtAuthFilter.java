package org.sopt.filter;

import static org.sopt.exception.ErrorCode.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.sopt.exception.ApiResponse;
import org.sopt.exception.ErrorCode;
import org.sopt.service.AuthService;
import org.sopt.util.JwtUtil;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements Filter {

	private final JwtUtil jwtUtil;
	private final AuthService authService;
	private final ObjectMapper objectMapper;

	// 인증 필요 없는 URL 목록 관리
	private static final List<String> EXCLUDE_URLS = Arrays.asList(
		"/login",
		"/singup",
		"/posts", // 게시글 전체 조회도 인증 없이 가능하게 하자.
		"posts/search" // 게시글 검색은 인증 없이 가능하므로
	);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
		IOException,
		ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String requestURI = httpRequest.getRequestURI();
		String method = httpRequest.getMethod();

		// 인증이 필요 없는 URL 인지 체크한다.
		if (isExcludedUrl(requestURI, method)) {
			chain.doFilter(request, response);
			return; // 인증 필요 없는 URL 이면 아래 작업 수행하지않고 리턴.
		}

		try {
			// authorization 헤더에서 토큰을 추출한다.
			String token = extractTokenFromHeader(httpRequest);
			if (token == null) {
				sendErrorResponse(httpResponse, MISSING_TOKEN);
				return;
			}

			// 토큰의 유효성 검증
			if (!jwtUtil.validateToken(token)) {
				sendErrorResponse(httpResponse, INVALID_TOKEN);
				return;
			}

			// 토큰에서 사용자 정보 추출
			Long userId = jwtUtil.getUserIdFromToken(token);

			// 사용자 존재 여부 확인하기
			authService.getUserById(userId);

			//요청에 사용자 정보 추가 (다음 단계에서 사용할 수 있도록)
			httpRequest.setAttribute("userId", userId);
			httpRequest.setAttribute("userEmail", jwtUtil.getEmailFromToken(token));

			chain.doFilter(request, response);

		} catch (Exception e) {
			sendErrorResponse(httpResponse, INVALID_TOKEN);
		}



	}

	// 인증이 필요 없는 URL 인지 확인
	private boolean isExcludedUrl(String requestURI, String method) {
		// GET 요청 중 게시글 조회 관련은 인증 불필요
		if ("GET".equals(method)) {
			return requestURI.equals("/posts") ||
				requestURI.startsWith("/posts/search") ||
				requestURI.matches("/posts/\\d+");  // /posts/{id} 패턴
		}

		// POST 요청 중 로그인, 회원가입은 인증 불필요
		return EXCLUDE_URLS.stream().anyMatch(requestURI::equals);
	}

	// AUTHORIZATION 헤더에서 토큰 추출
	private String extractTokenFromHeader(HttpServletRequest request) {
		String authorizationHeader = request.getHeader("Authorization");

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			return authorizationHeader.substring(7); // "Bearer " 제거
		}

		return null;
	}

	// 에러 응답 전송하기
	private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
		response.setStatus(errorCode.getStatus());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		ApiResponse<Void> apiResponse = ApiResponse.error(errorCode.getStatus(), errorCode.getMessage());
		String jsonResponse = objectMapper.writeValueAsString(apiResponse);

		response.getWriter().write(jsonResponse);
	}
}
