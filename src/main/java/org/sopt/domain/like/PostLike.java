package org.sopt.domain.like;

import org.sopt.global.common.entity.BaseEntity;
import org.sopt.domain.Post;
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
public class PostLike extends BaseEntity {

	@Id @GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false)
	private Post post;

	@Builder
	public PostLike(User user, Post post){
		this.user = user;
		this.post = post;
		// 생성자 안에서 연관관계 편의 메소드 설정까지하자.
		post.getLikes().add(this);

	}

	public static PostLike createPostLike(User user, Post post) {

		return PostLike.builder()
			.user(user)
			.post(post)
			.build();
	}

}

