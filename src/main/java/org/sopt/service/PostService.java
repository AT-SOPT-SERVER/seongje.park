package org.sopt.service;

import org.sopt.domain.Comment;
import org.sopt.domain.Post;
import org.sopt.domain.User;
import org.sopt.domain.enums.Tag;
import org.sopt.dto.CommentCreateRequest;
import org.sopt.dto.CommentEditRequest;
import org.sopt.dto.CommentResponse;
import org.sopt.dto.PostCommentResponse;
import org.sopt.dto.PostRequest;
import org.sopt.dto.PostResponse;
import org.sopt.dto.PostSearchCondition;
import org.sopt.dto.PostSimpleResponse;
import org.sopt.exception.AuthorityException;
import org.sopt.exception.CommentException;
import org.sopt.exception.ErrorCode;
import org.sopt.exception.PostException;
import org.sopt.exception.UserException;
import org.sopt.repository.CommentRepository;
import org.sopt.repository.UserRepository;
import org.sopt.util.PostIdUtil;
import org.sopt.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.sopt.exception.ErrorCode.*;

@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    // PostService 에서 member 를 가져와야 하므로 의존성 추가
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository
    ,CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public PostSimpleResponse createPost(Long userId, PostRequest postRequest) {


        // userId 를 가진 회원이 존재하지 않는 상황은 발생해서는 안 됨.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        // 중복된 제목 존재하면 true 반환되므로 , 예외 처리.
        validateTitleExists(postRequest.title());
        // 예외 안터지면 아래줄로 내려와서 게시글 작성 진행 continue.
        Post post = new Post(user, postRequest.title() , postRequest.content(), postRequest.tag()); // 생성자 안에서 연관관계 설정 완료.
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

        return new PostResponse(post.getId(), post.getTitle(), post.getContent(), post.getUser().getName());
    }

    public List<PostResponse> searchPostByAuthor(String userName){
        List<Post> post = postRepository.findByUserNameContaining(userName)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));

        return post.stream()
                .map(p -> new PostResponse(p.getId(), p.getTitle(),
                        p.getContent(), p.getUser().getName()))
                .collect(Collectors.toList());

    }

    private void validateTitleExists(String title) {
        if (postRepository.existsByTitle(title)) {
            throw new PostException(DUPLICATE_TITLE);
        }
    }

    // 댓글 작성 기능
    @Transactional
    public PostCommentResponse writeComment(Long userId, Long postId, CommentCreateRequest createRequest) {

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
        return PostCommentResponse.from(post);


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

        return CommentResponse.from(comment);

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

    }

    public List<CommentResponse> getAllCommentsByPost(Long postId) {
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

    public Page<PostSimpleResponse> searchPostByTitleAndUserName(Pageable pageable , PostSearchCondition condition) {
        return postRepository.searchByTitleAndAuthor(pageable, condition);

    }
}