package org.sopt.repository.comment;

import java.util.List;

import org.sopt.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findCommentsByPostId(Long id);
}
