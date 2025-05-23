package org.sopt.domain.like;

import org.sopt.common.entity.BaseEntity;
import org.sopt.domain.Comment;
import org.sopt.domain.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class CommentLike extends BaseEntity {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "comment_id", nullable = false)
	private Comment comment;

	@Builder
	public CommentLike(User user, Comment comment) {
		this.user = user;
		this.comment = comment;

		// 편의 메소드 설정
		comment.getLikes().add(this);
	}

	public static CommentLike createCommentLike(User user, Comment comment){

		return CommentLike.builder()
			.user(user)
			.comment(comment)
			.build();
	}

}



