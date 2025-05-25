package org.sopt.controller.post;

import org.sopt.dto.CommentCreateRequest;
import org.sopt.dto.CommentEditRequest;
import org.sopt.dto.CommentResponse;
import org.sopt.dto.PostCommentResponse;
import org.sopt.dto.PostRequest;
import org.sopt.dto.PostResponse;
import org.sopt.dto.PostSearchCondition;
import org.sopt.dto.PostSimpleResponse;
import org.sopt.exception.ApiResponse;
import org.sopt.service.PostService;
import org.sopt.validator.PostValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<ApiResponse<PostSimpleResponse>> createPost(
            @RequestHeader Long userId,
            @RequestBody final PostRequest postCreateRequest) {

        PostValidator.validateTitle(postCreateRequest.title());
        // 제목이 비어있지 않고, 제목이 30자를 넘지않는다면 이제 게시글을 작성하자.
        // 검증사항 추가 ++ : 게시글 내용은 1,000자를 넘지 않아야 한다.
        PostSimpleResponse createdPost = postService.createPost(userId, postCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdPost, "게시글 생성 성공"));
    }


    // 모든 게시글 조회(10개 단위로 페이징)
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<Page<PostSimpleResponse>>> getAllPosts(
        @PageableDefault(size = 10) Pageable pageable) {

        Page<PostSimpleResponse> posts = postService.getAllPosts(pageable);
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

    // title, author 로 게시글 조회(페이징까지 같이)
    // 페이징 사이즈는 기본 10개로 설정
    @GetMapping("/posts/search")
    public ResponseEntity<ApiResponse<Page<PostSimpleResponse>>> getPostByTitleAndUserName(
        @PageableDefault(size = 10) Pageable pageable, @ModelAttribute PostSearchCondition condition) {

        Page<PostSimpleResponse> posts = postService.searchPostByTitleAndUserName(pageable, condition);

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

    // 댓글 작성 (어떤 게시물에 누가 댓글을 작성할 것인지 알아야함)
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<PostCommentResponse>> makeComment(
        @RequestHeader Long userId,
        @PathVariable("postId") Long postId, @RequestBody CommentCreateRequest createRequest){

        PostCommentResponse createdComment = postService.writeComment(userId, postId, createRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(createdComment, "댓글 작성 성공"));

    }

    // 댓글 수정 .
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> editComment(
        @RequestHeader Long userId, @PathVariable("commentId") Long commentId,
        @RequestBody CommentEditRequest editRequest){

        CommentResponse editedComment = postService.editComment(userId, commentId, editRequest);

        return ResponseEntity.ok(ApiResponse.success(editedComment, "댓글이 성공적으로 수정되었습니다."));

    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
        @RequestHeader Long userId, @PathVariable("commentId") Long commentId){

        postService.deleteComment(userId, commentId);

        return ResponseEntity.ok(ApiResponse.success(null, "댓글 삭제가 성공적으로 수행되었습니다."));
    }

    // 댓글 조회 (특정 게시물의 댓글 모두 조회)
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getAllCommentsByPost(
        @PathVariable("postId") Long postId) {

        List<CommentResponse> comments = postService.getAllCommentsByPost(postId);

        return ResponseEntity.ok(ApiResponse.success(comments, "댓글 조회 성공"));
    }




}
