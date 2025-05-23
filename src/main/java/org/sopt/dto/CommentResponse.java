package org.sopt.dto;

import java.time.LocalDateTime;

import org.sopt.domain.Comment;

public record CommentResponse(
	Long id,
	String author, // 작성자
	String content, // 댓글 내용
	LocalDateTime createdAt, // 작성된 시간

	// 추가
	Integer likeCount
) {

	public static CommentResponse from(Comment comment) {
		return new CommentResponse(comment.getId(), comment.getAuthor().getName(),
			comment.getContent(), comment.getCreatedAt(), comment.getLikeCount());
	}
}
