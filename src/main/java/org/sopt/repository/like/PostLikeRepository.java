package org.sopt.repository.like;

import org.sopt.domain.Post;
import org.sopt.domain.User;
import org.sopt.domain.like.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

	boolean existsByPostAndUser(Post post, User user);

}
