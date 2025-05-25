package org.sopt.domain.like;

import org.sopt.global.common.entity.BaseEntity;
import org.sopt.domain.Comment;
import org.sopt.domain.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(uniqueConstraints = {
	@UniqueConstraint(name = "uk_post_like_user_post", columnNames = {"USER_ID", "COMMENT_ID"})
})
public class CommentLike extends BaseEntity {

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMMENT_ID", nullable = false)
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



