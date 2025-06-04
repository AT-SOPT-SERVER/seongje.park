package org.sopt.controller.like;

import org.sopt.dto.post.PostResponse;
import org.sopt.exception.ApiResponse;
import org.sopt.exception.ErrorCode;
import org.sopt.exception.PostException;
import org.sopt.service.PostLikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
// 게시글에 좋아요 수 증가
public class PostLikeController {

	private final PostLikeService postLikeService;

	@PatchMapping("/posts/{postId}/likes")
	public ResponseEntity<ApiResponse<PostResponse>> addLikeFromPost(
		@PathVariable("postId") Long postId, HttpServletRequest request) {

		Long userId = getUserIdFromRequest(request);
		PostResponse post = postLikeService.likePost(postId, userId);
		return ResponseEntity.ok(ApiResponse.success(post, "좋아요 누르기 성공"));
	}

	private Long getUserIdFromRequest(HttpServletRequest request) {
		Long userId = (Long) request.getAttribute("userId");
		if (userId == null) {
			throw new PostException(ErrorCode.INVALID_TOKEN);
		}
		return userId;
	}


}
