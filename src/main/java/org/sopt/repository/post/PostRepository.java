package org.sopt.repository.post;

import org.sopt.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> , PostRepositoryCustom {

    boolean existsByTitle(String title);

    Optional<Post> findByTitleContaining(String title);

    // Page<Post> findAllByOrderByCreatedAtAsc(Pageable pageable);

    Optional<List<Post>> findByUserNameContaining(String userName);


    


}