package org.sopt.repository.like;

import java.util.Optional;

import org.sopt.domain.Post;
import org.sopt.domain.User;
import org.sopt.domain.like.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

	boolean existsByPostAndUser(Post post, User user);

	// 특정 게시글의 좋아요 수
	Long countByPostId(Long postId);

	Optional<PostLike> findByPostAndUser(Post post, User user);
}
