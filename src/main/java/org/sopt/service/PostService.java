package org.sopt.service;

import org.sopt.domain.Post;
import org.sopt.domain.User;
import org.sopt.domain.enums.Tag;
import org.sopt.dto.PostResponse;
import org.sopt.exception.ErrorCode;
import org.sopt.exception.PostException;
import org.sopt.exception.UserException;
import org.sopt.repository.UserRepository;
import org.sopt.util.PostIdUtil;
import org.sopt.repository.PostRepository;
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

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createPost(Long userId, String title, String content, Tag tag) {

        // userId 를 가진 회원이 존재하지 않는 상황은 발생해서는 안 됨.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        // 중복된 제목 존재하면 true 반환되므로 , 예외 처리.
        validateTitleExists(title);
        // 예외 안터지면 아래줄로 내려와서 게시글 작성 진행 continue.
        Post post = new Post(user, title , content, tag); // 생성자 안에서 연관관계 설정 완료.
        postRepository.save(post);

    }

    // 게시글 전체조회에서는 , 제목과 게시글 작성자만 보이게.
    // 최신순으로 조회해야한다.
    public List<PostResponse> getAllPosts() {

        List<Post> postList = postRepository.findAllByOrderByCreatedAtAsc();

        return postList.stream()
                .map(p -> PostResponse.ofTitleAndUser(p.getId(), p.getTitle(),
                        p.getUser().getName()))
                .collect(Collectors.toList());
    }


    // 게시글 단건 상세 조회에서는 , 제목과 내용, 작성자가 모두 보이도록 설정
    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));

        return new PostResponse(post.getId(), post.getTitle(), post.getContent(), post.getUser().getName());
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

    public PostResponse searchPostByAuthor(String userName){
        Post post = postRepository.findByUserNameContaining(userName)
                .orElseThrow(() -> new PostException(POST_NOT_FOUND));

        return new PostResponse(post.getId(), post.getTitle(), post.getContent(), post.getUser().getName());

    }

    private void validateTitleExists(String title) {
        if (postRepository.existsByTitle(title)) {
            throw new PostException(DUPLICATE_TITLE);
        }
    }


}