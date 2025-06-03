package org.sopt.repository.comment;

import java.util.List;
import java.util.Optional;

import org.sopt.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findCommentsByPostId(Long id);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select c from Comment c join fetch c.author u join fetch c.post p where c.id = :id")
	Optional<Comment> findByIdWithLock(@Param("id") Long id);
}
