package org.sopt.service;

import org.sopt.domain.Post;
import org.sopt.dto.PostResponse;
import org.sopt.exception.ErrorCode;
import org.sopt.exception.PostException;
import org.sopt.util.PostIdUtil;
import org.sopt.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional
    public void createPost(String title) {
        // 중복된 제목 존재하면 true 반환되므로 , 예외 처리.
        if (postRepository.existsByTitle(title)) {
            throw new PostException(ErrorCode.DUPLICATE_TITLE);
        }
        // 예외 안터지면 아래줄로 내려와서 게시글 작성 진행 continue.

        Post post = new Post(title);
        postRepository.save(post);

    }

    public List<PostResponse> getAllPosts() {

        List<Post> postList = postRepository.findAll();

        return postList.stream()
                .map(p -> new PostResponse(p.getId(), p.getTitle()))
                .collect(Collectors.toList());
    }

    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

        return new PostResponse(post.getId(), post.getTitle());

    }


    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

        postRepository.delete(post);
    }

    // 게시글 수정 기능(게시글 id 와 수정 내용을 주면, 해당 id 를 가진 게시글을 수정)
    @Transactional
    public void editPost(Long id , String title){

        if (postRepository.existsByTitle(title)) {
            throw new PostException(ErrorCode.DUPLICATE_TITLE);
        }

        Post foundPost = postRepository.findById(id)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

        foundPost.changeTitle(title);
        // 영속성 컨텍스트는 변경감지 기능이 있기 때문에, save 따로 해줄 필요 없음

        // 디버깅용
        System.out.println("수정 성공 : " + foundPost.getTitle());

    }

    public PostResponse getPostByTitle(String title) {
        Post post = postRepository.getPostByTitle(title)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

        return new PostResponse(post.getId(), post.getTitle());
    }


}