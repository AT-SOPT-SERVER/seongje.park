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

	@Lock(LockModeType.PESSIMISTIC_READ) // S LOCK
	// X 락을 가져오면, 같은 댓글에 대한 조회가 불가능하므로 조회 성능에 문제 있을 수 있음.
	// 같은 댓글에 대한 수정만 막도록 하자.
	@Query("select c from Comment c join fetch c.author u join fetch c.post p where c.id = :id")
	Optional<Comment> findByIdWithLock(@Param("id") Long id);
}
