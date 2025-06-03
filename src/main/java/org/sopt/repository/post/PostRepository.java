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

    @Query("SELECT p from Post p join fetch p.user u where p.id = :id")
    Optional<Post> findByIdWithUser(@Param("id") Long id);


    // 좋아요 처리에 대한 동시성 보장이 100% 되어있지 않음. race condition 발생 가능.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT p from Post p where p.id = :id")
    Optional<Post> findByIdWithLock(@Param("id") Long id);
}