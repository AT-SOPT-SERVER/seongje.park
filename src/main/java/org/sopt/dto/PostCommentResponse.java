package org.sopt.dto;

import java.util.List;

import org.sopt.domain.Comment;
import org.sopt.domain.Post;

// 좋아요 수 추가됨
public record PostCommentResponse(
	Long id,
	String title,
	String userName,
	List<CommentResponse> comments,
	// 좋아요 추가
	Integer likeCount

) {

	public static PostCommentResponse from(Post post) {
		List<CommentResponse> commentDtos = post.getComments().stream()
			.map(CommentResponse::from)
			.toList();

		return new PostCommentResponse(
			post.getId(),
			post.getTitle(),
			post.getUser().getName(),
			commentDtos,
			post.getLikeCount()
		);

	}
}
