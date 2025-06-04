package org.sopt.repository.post;

import org.sopt.domain.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

import jakarta.persistence.LockModeType;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> , PostRepositoryCustom {

    boolean existsByTitle(String title);

    Optional<Post> findByTitleContaining(String title);

    // Page<Post> findAllByOrderByCreatedAtAsc(Pageable pageable);

    @Query("SELECT p from Post p join fetch p.user u where u.name LIKE %:userName%")
    List<Post> findByUserNameContaining(String userName);


    @EntityGraph(attributePaths = {"user"})
    Optional<Post> findPostWithUserById(Long id);


    // 좋아요 처리에 대한 동시성 보장이 100% 되어있지 않음. race condition 발생 가능.
    // X 락으로 설정. UPDATE 해야하므로
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"user"})
    Optional<Post> findCommentWithLockById(Long id);
}