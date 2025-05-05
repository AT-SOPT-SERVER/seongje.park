package org.sopt.repository;

import org.sopt.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    boolean existsByTitle(String title);

    Optional<Post> findByTitleContaining(String title);

    List<Post> findAllByOrderByCreatedAtAsc();

    Optional<List<Post>> findByUserNameContaining(String userName);


    


}