package org.sopt.service;

import org.sopt.domain.Post;
import org.sopt.util.PostIdUtil;
import org.sopt.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public void createPost(String title) {
        // 중복된 제목 존재하면 true 반환되므로 , 예외 처리.
        if (postRepository.existsByTitle(title)) {
            throw new RuntimeException("중복된 게시글이 존재합니다.");
        }
        // 예외 안터지면 아래줄로 내려와서 게시글 작성 진행 continue.

        Post post = new Post(title);
        postRepository.save(post);

    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 id 를 가진 게시글이 존재하지 않습니다."));
    }


    public void deletePostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 id 를 가진 게시글이 존재하지 않습니다."));

        postRepository.delete(post);
    }

    // 게시글 수정 기능(게시글 id 와 수정 내용을 주면, 해당 id 를 가진 게시글을 수정)
    public void editPost(Long id , String title){

        if (postRepository.existsByTitle(title)) {
            throw new RuntimeException("중복된 게시글이 존재합니다.");
        }

        Post foundPost = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 id 를 가진 게시글이 존재하지 않습니다."));

        foundPost.changeTitle(title);
        // 영속성 컨텍스트는 변경감지 기능이 있기 때문에, save 따로 해줄 필요 없음

        // 디버깅용
        System.out.println("수정 성공 : " + foundPost.getTitle());

    }

    public Post getPostByTitle(String title) {
        return postRepository.getPostByTitle(title)
                .orElseThrow(() -> new RuntimeException("해당 제목을 가진 게시글이 존재하지 않습니다."));
    }


}