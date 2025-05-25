package org.sopt.domain.like;

import org.sopt.global.common.entity.BaseEntity;
import org.sopt.domain.Post;
import org.sopt.domain.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
	@UniqueConstraint(name = "uk_post_like_user_post", columnNames = {"USER_ID", "POST_ID"})
})
// 유저ID 와 게시글ID에 대해 유니크 제약조건(인덱스도 자동 생성) 을 걸어준 이유
// 사용자가 좋아요 버튼을 빠르게 2번 클릭했다고 가정하면,
// PostLike 엔티티 2개가 모두 저장될 수 있음. 우리가 원한 건 이게 아님.
// 유니크 제약조건을 주면, 첫번째 postlike 엔티티가 저장되고 나면,
//두번째 postlike 엔티티는 (user_id, post_id) 값이 똑같으므로 db 가 차단한다.
// 동시에, existsByPostAndUser() 쿼리도 매우 빨라진다.

public class PostLike extends BaseEntity {

	@Id @GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "POST_ID", nullable = false)
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

