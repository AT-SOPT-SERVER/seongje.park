package org.sopt.repository.like;

import java.util.Optional;

import org.sopt.domain.Comment;
import org.sopt.domain.User;
import org.sopt.domain.like.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

	boolean existsByCommentAndUser(Comment comment, User user);

	long countByCommentId(Long commentId);

	Optional<CommentLike> findByCommentAndUser(Comment comment, User user);
}
