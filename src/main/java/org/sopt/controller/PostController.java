package org.sopt.controller;

import org.sopt.domain.Post;
import org.sopt.service.PostService;
import org.sopt.util.StringControlUtil;

import java.util.List;
import java.util.Optional;

public class PostController {
    private final PostService postService = new PostService();

    // 게시글 작성하기
    public void createPost(final String title) {
        validateTitleEmptyAndLength(title);

        // 제목이 비어있지 않고, 제목이 30자를 넘지않는다면 이제 게시글을 작성하자.
        postService.createPost(title);
    }


    // 모든 게시글 조회
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    // id 로 게시글 조회
    public Post getPostById(Long id) {
        return postService.getPostById(id);
    }

    // 제목으로 게시글 조회
    public Post getPostByTitle(String title){
        return postService.getPostByTitle(title);
    }

    // 게시글 검색 기능
    public Post searchPostByCondition(Long id, String title){
        return postService.searchPostByCondition(id,title);
    }


    // id 로 게시글 삭제
    public boolean deletePostById(Long id) {
        return postService.deletePostById(id);
    }

    // 게시글 수정
    public void editPost(Long id, String title){

        validateTitleEmptyAndLength(title);
        postService.editPost(id, title);
    }

    private static void validateTitleEmptyAndLength(String title) {
        if (title == null){
            throw new RuntimeException("게시글 제목이 비어있습니다.");
        }
        // 제목이 30자를 넘지 않는지 검증 (+추가 기능 : 제목에 이모지를 허용하고, 이모지를 1글자가 되도록)
        if (StringControlUtil.countCharacters(title) > 30) {
            throw new RuntimeException("게시글 제목이 30자를 초과했습니다. 현재 글자수 : " +
                    StringControlUtil.countCharacters(title));
        }
    }
}
