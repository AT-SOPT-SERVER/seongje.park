package org.sopt.service;

import org.sopt.domain.Comment;
import org.sopt.domain.Post;
import org.sopt.domain.User;
import org.sopt.dto.comment.CommentCreateRequest;
import org.sopt.dto.comment.CommentEditRequest;
import org.sopt.dto.comment.CommentResponse;
import org.sopt.dto.post.PostResponse;
import org.sopt.dto.post.PostRequest;
import org.sopt.dto.PostSearchCondition;
import org.sopt.dto.post.PostSimpleResponse;
import org.sopt.exception.AuthorityException;
import org.sopt.exception.CommentException;
import org.sopt.exception.PostException;
import org.sopt.exception.UserException;
import org.sopt.repository.comment.CommentRepository;
import org.sopt.repository.user.UserRepository;
import org.sopt.repository.post.PostRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.sopt.exception.ErrorCode.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    // PostService 에서 member 를 가져와야 하므로 의존성 추가
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    // 비동기 서비스 추가
    private final AsyncService asyncService;

    // 게시글 상세 조회 (댓글조회 및  좋아요 수 계산을 병렬 조회)
    public PostResponse getPostByIdWithAsync(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new PostException(POST_NOT_FOUND));

        try {
            // 댓글 리스트와 게시글 좋아요 수를 병렬로 조회
            CompletableFuture<List<CommentResponse>> commentsFuture =
                asyncService.getCommentsWithLikeCountAsync(id);
            CompletableFuture<Long> postLikeCountFuture =
                asyncService.getPostLikeCountAsync(id);

            // 두 작업이 모두 완료될 때까지 대기
            CompletableFuture<Void> allOf = CompletableFuture.allOf(
                commentsFuture, postLikeCountFuture);

            return allOf.thenApply(v -> {
                List<CommentResponse> comments = commentsFuture.join();
                Long postLikeCount = postLikeCountFuture.join();

                return new PostResponse(
                    post.getId(),
                    post.getTitle(),
                    post.getContent(),
                    post.getUser().getName(),
                    comments,
                    postLikeCount.intValue(),
                    post.getTags()
                );
            }).get(3, TimeUnit.SECONDS);

        } catch (Exception e) {
            // 비동기 처리 실패 시 동기 방식으로 폴백
            return getPostById(id);
        }
    }

    // 댓글만 비동기 처리
    public List<CommentResponse> getAllCommentsByPostAsync(Long postId){
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new PostException(POST_NOT_FOUND));

        try{
            return asyncService.getCommentsWithLikeCountAsync(postId)
                .get(2, TimeUnit.SECONDS);
        } catch (Exception e){
            return getAllCommentsByPost(postId);
            // 비동기 처리 실패시 동기 방식으로 풀백
        }


    }


    @Transactional
    public PostSimpleResponse createPost(Long userId, PostRequest postRequest) {


        // userId 를 가진 회원이 존재하지 않는 상황은 발생해서는 안 됨.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        // 중복된 제목 존재하면 true 반환되므로 , 예외 처리.
        validateTitleExists(postRequest.title());
        // 예외 안터지면 아래줄로 내려와서 게시글 작성 진행 continue.
        Post post = Post.makePost(user, postRequest);
        // Post post = new Post(user, postRequest.title() , postRequest.content(), postRequest.tags()); // 생성자 안에서 연관관계 설정 완료.
        postRepository.save(post);

        return PostSimpleResponse.from(post);

    }

    // 게시글 전체조회에서는 , 제목과 게시글 작성자만 보이게.
    // 최신순으로 조회해야한다.
    public Page<PostSimpleResponse> getAllPosts(Pageable pageable) {

        // Page<Post> postList = postRepository.findAllByOrderByCreatedAtAsc(pageable);
        Page<Post> postList = postRepository.findAll(pageable);

        // 근데.. 주석처리한 방식이랑 아래 방식 중 뭘 사용해야할까?
        // 첫번째 방식은 고정된 정렬 방식임. 동적 정렬이 불가
        // 반면, 두번째 방식은 클라이언트가 sort 에 정렬 방식을 주면, 서버에서 동적으로 정렬이 가능.


        return postList.map(PostSimpleResponse::from);
    }

    // querydsl 로 동적 쿼리 검색 (제목, 작성자 기준)
    public Page<PostSimpleResponse> searchPostByTitleAndUserName(Pageable pageable , PostSearchCondition condition) {
        return postRepository.searchByTitleAndAuthor(pageable, condition);

    }

    // 게시글 단건 상세 조회에서는 , 제목과 내용, 작성자가 모두 보이도록 설정
    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));

        return PostResponse.from(post);

    }


    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));

        postRepository.delete(post);
    }

    // 게시글 수정 기능(게시글 id 와 수정 내용을 주면, 해당 id 를 가진 게시글을 수정)

    @Transactional
    public void editPost(Long id , String title){

        validateTitleExists(title);

        Post foundPost = postRepository.findById(id)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));

        foundPost.changeTitle(title);
        // 영속성 컨텍스트는 변경감지 기능이 있기 때문에, save 따로 해줄 필요 없음

        // 디버깅용
        System.out.println("수정 성공 : " + foundPost.getTitle());

    }
    public PostResponse searchPostByTitle(String title) {
        Post post = postRepository.findByTitleContaining(title)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));

        return PostResponse.from(post);
    }

    public List<PostResponse> searchPostByAuthor(String userName){
        List<Post> post = postRepository.findByUserNameContaining(userName)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));

        return post.stream()
                .map(PostResponse::from)
                .collect(Collectors.toList());

    }

    private void validateTitleExists(String title) {
        if (postRepository.existsByTitle(title)) {
            throw new PostException(DUPLICATE_TITLE);
        }
    }

    // 댓글 작성 기능
    @Transactional
    @CacheEvict(value = "postComments" , key = "#postId")
    public PostResponse writeComment(Long userId, Long postId, CommentCreateRequest createRequest) {

        log.info("댓글 작성으로 캐시 무효화: postId = {}", postId);

        // userId로 회원 조회
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        // postId 로 게시글 조회
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new PostException(POST_NOT_FOUND));

        Comment comment = Comment.createComment(createRequest.content(), user, post);

        // builder 안에서 연관관계 세팅 및 양방향 연결 완료.
        commentRepository.save(comment);

        // 댓글 작성 결과 반환
        return PostResponse.from(post);


    }

    // 댓글 수정 기능
    @Transactional
    public CommentResponse editComment(Long userId, Long commentId, CommentEditRequest editRequest) {

        // userId로 회원 조회
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));


        // commentId 로 댓글 조회
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentException(COMMENT_NOT_FOUND));

        // 댓글을 수정할 권한은 댓글 작성자에게만 있음. 이것을 검증해야한다.
        checkAuthorization(comment, user);

        // 권한 검증이 성공하면 댓글 수정 가능하다.
        comment.updateContent(editRequest.content());

        // 수정된 댓글이 속한 게시글의 댓글 목록 캐시 무효화
        evictPostCommentsCache(comment.getPost().getId());
        
        return CommentResponse.from(comment);

    }

    @CacheEvict(value = "postComments", key = "#postId")
    public void evictPostCommentsCache(Long postId) {
        log.info("댓글 목록 캐시 무효화: postId = {}", postId);
    }



    private static void checkAuthorization(Comment comment, User user) {
        if (!comment.getAuthor().getId().equals(user.getId())) {
            // 수정 또는 삭제할 댓글을 작성한 유저의 id 와, header 로 받은 user 의 id 가 일치하지 않는다면 예외 반환
            throw new AuthorityException(AUTHORIZATION_FAIL);
        }
    }

    // 댓글 삭제 기능
    public void deleteComment(Long userId, Long commentId) {

        // userId로 회원 조회
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        // commentId 로 댓글 조회
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentException(COMMENT_NOT_FOUND));

        // 댓글을 삭제할 권한은 댓글 작성자에게만 있음. 이것을 검증해야한다.
        checkAuthorization(comment, user);

        // 권한 검증이 성공하면 삭제 수행한다.
        commentRepository.delete(comment);

        // 삭제될 댓글이 속한 게시글 ID 저장
        Long postId = comment.getPost().getId();
        
        // 삭제된 댓글이 속한 게시글의 댓글 목록 캐시 무효화
        evictPostCommentsCache(postId);

    }

    @Cacheable(value = "postComments", key = "#postId")
    public List<CommentResponse> getAllCommentsByPost(Long postId) {
        log.info("DB에서 댓글 목록 조회: postId = {}", postId);

        // postId 로 게시글 조회
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new PostException(POST_NOT_FOUND));


        List<Comment> comments = commentRepository.findCommentsByPostId(post.getId());
        // 또는, post.getComments() 를 통해 가져올 수도 있겠다.

        List<CommentResponse> list = comments.stream()
            .map(comment -> CommentResponse.from(comment))
            .toList();

        return list;

    }


}