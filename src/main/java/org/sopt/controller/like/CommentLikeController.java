package org.sopt.controller.like;

import org.sopt.dto.CommentResponse;
import org.sopt.dto.PostCommentResponse;
import org.sopt.exception.ApiResponse;
import org.sopt.service.CommentLikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommentLikeController {

	private final CommentLikeService commentLikeService;

	@PatchMapping("/comments/{commentId}/likes")
	public ResponseEntity<ApiResponse<CommentResponse>> addLikeFromComment(
		@PathVariable Long commentId, @RequestHeader Long userId) {

		CommentResponse comment = commentLikeService.likeComment(commentId, userId);

		return ResponseEntity.ok(ApiResponse.success(comment, "댓글에 좋아요 성공"));
	}

}
