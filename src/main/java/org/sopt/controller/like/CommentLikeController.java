package org.sopt.controller.like;

import org.sopt.dto.comment.CommentResponse;
import org.sopt.exception.ApiResponse;
import org.sopt.exception.ErrorCode;
import org.sopt.exception.PostException;
import org.sopt.service.CommentLikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommentLikeController {

	private final CommentLikeService commentLikeService;

	@PatchMapping("/comments/{commentId}/likes")
	public ResponseEntity<ApiResponse<CommentResponse>> addLikeFromComment(
		@PathVariable Long commentId, HttpServletRequest request) {

		Long userId = getUserIdFromRequest(request);
		CommentResponse comment = commentLikeService.likeComment(commentId, userId);

		return ResponseEntity.ok(ApiResponse.success(comment, "댓글에 좋아요 성공"));
	}


	private Long getUserIdFromRequest(HttpServletRequest request) {
		Long userId = (Long) request.getAttribute("userId");
		if (userId == null) {
			throw new PostException(ErrorCode.INVALID_TOKEN);
		}
		return userId;
	}

}
