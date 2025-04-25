package org.sopt.controller;

import org.sopt.domain.Post;
import org.sopt.dto.PostRequest;
import org.sopt.service.PostService;
import org.sopt.util.StringControlUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 게시글 작성하기
    @PostMapping("/post")
    public void createPost(@RequestBody final PostRequest postRequest) {

        validateTitleEmptyAndLength(postRequest.getTitle());
        // 제목이 비어있지 않고, 제목이 30자를 넘지않는다면 이제 게시글을 작성하자.
        postService.createPost(postRequest.getTitle());
    }


    // 모든 게시글 조회
    @GetMapping("/posts")
    public ResponseEntity<?> getAllPosts() {

        return ResponseEntity.ok(postService.getAllPosts());
    }

    // id 로 게시글 조회
    @GetMapping("/posts/{id}")
    public Post getPostById(@PathVariable("id") Long id) {

        return postService.getPostById(id);
    }


    //게시글 삭제
    @DeleteMapping("/posts/{id}")
    public boolean deletePostById(@PathVariable("id") Long id) {

        return postService.deletePostById(id);
    }

    // 게시글 수정
    @PatchMapping("/posts/{id")
    public void editPost(@PathVariable("id") Long id, @RequestBody PostRequest editRequest){

        validateTitleEmptyAndLength(editRequest.getTitle());
        postService.editPost(id, editRequest.getTitle());
    }

    private static void validateTitleEmptyAndLength(String title) {
        if (title == null || title.isBlank()){
            throw new RuntimeException("게시글 제목이 비어있습니다.");
        }
        // 제목이 30자를 넘지 않는지 검증 (+추가 기능 : 제목에 이모지를 허용하고, 이모지를 1글자가 되도록)
        if (StringControlUtil.countCharacters(title) > 30) {
            throw new RuntimeException("게시글 제목이 30자를 초과했습니다. 현재 글자수 : " +
                    StringControlUtil.countCharacters(title));
        }
    }
}
