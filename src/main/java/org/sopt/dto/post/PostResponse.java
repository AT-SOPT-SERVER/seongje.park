package org.sopt.dto.post;

import java.util.List;

import org.sopt.domain.Post;
import org.sopt.domain.enums.Tag;
import org.sopt.dto.comment.CommentResponse;
// 게시글 상세조회시 응답 DTO

// 좋아요 수 추가됨
public record PostResponse(
	Long id,
	String title,
	String content,
	String userName,
	List<CommentResponse> comments,
	// 좋아요 추가
	Integer likeCount,
	// 태그도 출력
	List<Tag> tags

) {

	public static PostResponse from(Post post) {
		List<CommentResponse> commentDtos = post.getComments().stream()
			.map(CommentResponse::from)
			.toList();
		// POST 가져오는 쿼리 한번 실행. (1)
		// COMMENT 목록 가져오는 쿼리 한번 실행(N개의 comment 가져옴)
		// N개의 comment 에 대해, 작성자를 가져와야하므로 쿼리 N개 나감.

		return new PostResponse(
			post.getId(),
			post.getTitle(),
			post.getContent(),
			post.getUser().getName(),
			commentDtos,
			post.getLikeCount(),
			post.getTags()
		);

	}
}
