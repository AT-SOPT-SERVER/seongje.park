package org.sopt.controller;

import org.sopt.domain.Post;
import org.sopt.dto.PostRequest;
import org.sopt.dto.PostResponse;
import org.sopt.exception.ApiResponse;
import org.sopt.exception.ErrorCode;
import org.sopt.exception.PostException;
import org.sopt.service.PostService;
import org.sopt.util.StringControlUtil;
import org.sopt.validator.PostValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 게시글 작성하기
    @PostMapping("/post")
    public ResponseEntity<ApiResponse<Void>> createPost(
            @RequestHeader Long userId,
            @RequestBody final PostRequest postCreateRequest) {

        PostValidator.validateTitle(postCreateRequest.title());
        // 제목이 비어있지 않고, 제목이 30자를 넘지않는다면 이제 게시글을 작성하자.
        // 검증사항 추가 ++ : 게시글 내용은 1,000자를 넘지 않아야 한다.
        postService.createPost(userId,
                postCreateRequest.title(), postCreateRequest.content(),
                postCreateRequest.tag());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, "게시글 생성 성공"));
    }


    // 모든 게시글 조회
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllPosts() {

        List<PostResponse> posts = postService.getAllPosts();
        return ResponseEntity.ok(ApiResponse.success(posts));
    }

    // id 로 게시글 조회
    @GetMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> getPostById(@PathVariable("id") Long id) {

        PostResponse post = postService.getPostById(id);
        return ResponseEntity.ok(ApiResponse.success(post));
    }

    // title 로 게시글 조회
    @GetMapping("/posts/search/title")
    public ResponseEntity<ApiResponse<PostResponse>> getPostByTitle(@RequestParam String title) {

        PostResponse post = postService.searchPostByTitle(title);
        return ResponseEntity.ok(ApiResponse.success(post));
    }

    // userName 으로 게시글 조회
    @GetMapping("/posts/search/userName")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getPostsByUserName(@RequestParam String userName) {

        List<PostResponse> posts = postService.searchPostByAuthor(userName);
        return ResponseEntity.ok(ApiResponse.success(posts));

    }

    //게시글 삭제
    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePostById(@PathVariable("id") Long id) {

        postService.deletePost(id);
        // api 응답필요
        return ResponseEntity.ok(ApiResponse.success(null, "게시글이 성공적으로 삭제되었습니다."));
    }

    // 게시글 수정
    @PatchMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<Void>> changePostTitle(@PathVariable("id") Long id, @RequestBody PostRequest editRequest){

        PostValidator.validateTitle(editRequest.title());
        postService.editPost(id, editRequest.title());
        // api 응답 필요

        return ResponseEntity.ok(ApiResponse.success(null, "게시글이 성공적으로 수정되었습니다."));


    }


}
