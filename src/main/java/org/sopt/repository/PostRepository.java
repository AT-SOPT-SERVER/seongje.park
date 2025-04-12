package org.sopt.repository;

import org.sopt.domain.Post;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class PostRepository {

    // db 사용 X. 내부 메모리 저장소로 postMap 사용
    private HashMap<Long, Post> postMap = new HashMap<>();


    public void save(Post post) {
        postMap.put(post.getId(), post);
    }

    public List<Post> findAll() {
        return new ArrayList<>(postMap.values());
    }

    public Post findPostById(Long id) {
        return postMap.get(id);
    }

    public boolean delete(Long id) {
        return postMap.remove(id) != null;
    }

    public Post getPostByTitle(String title) {
        return postMap.values().stream()
                .filter(post -> post.getTitle().equals(title))
                .findFirst()
                .orElse(null);
    }

    // 게시글 검색 (id, title 검색)
    public Post searchPostByCondition(Long id, String title){

        return postMap.values().stream()
                .filter(post ->
                        (id == null || post.getId().equals(id)) &&
                                (title == null || post.getTitle().equals(title))
                )
                .findFirst()
                .orElse(null);
    }



}