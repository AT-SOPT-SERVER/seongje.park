package org.sopt.controller;

import org.sopt.domain.Post;
import org.sopt.service.PostService;

import java.util.List;

public class PostController {

    private PostService postService = new PostService();

    private int postId = 0;

    // 게시글 등록
    public void createPost(String title) {
        postService.createPost(new Post(postId++, title));
    }

    // 게시글 전체 조회
    public List<Post> getAllPosts(){
        return postService.getAllPosts();
    }

    // 게시글 id 로 조회
    public Post getPostById(int id) {
        return postService.getPostById(id);

    }

    // 정상삭제되면 true 반환 / 그렇지 않으면 false
    public boolean deletePostById(int id) {
        return postService.deletePostById(id);
    }
}
