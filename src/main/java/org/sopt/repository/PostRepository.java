package org.sopt.repository;

import org.sopt.domain.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostRepository {

    List<Post> postList = new ArrayList<>();

    public void save(Post post) {
        postList.add(post);
    }

    public List<Post> findAll() {
        return postList;

    }

    // 게시글 id 로 조회
    public Post findById(int id){

        for (Post post : postList) {
            if (post.getId() == id) {
                return post;
            }
        }
        // 조회 실패시
        return null;

    }

    public boolean deleteById(int id) {

        // 기본값 false
        boolean result = false;

        // 삭제 성공시 true 반환
        for (Post post : postList) {
            if (post.getId() == id) {
                postList.remove(post);
                return true;
            }
        }

        // 삭제 실패시 기본값 반환
        return result;
    }
}
