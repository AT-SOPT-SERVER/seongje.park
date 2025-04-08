package org.sopt.service;

import org.sopt.domain.Post;
import org.sopt.util.PostIdUtil;
import org.sopt.repository.PostRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class PostService {

    private final PostRepository postRepository = new PostRepository();
    private final FileService fileService = new FileService();

    public void createPost(String title) {
        // 게시글 작성의 시간간격 검증
        validatePostInterval();
        // 중복된 제목의 게시글 작성되지 않도록 설정.
        validateDuplicateTitle(title);

        // 예외 안터지면 아래줄로 내려와서 게시글 작성 진행 continue.
        // PostIdUtil 클래스를 통해 ID 생성 책임을 따로 분리.
        Post post = Post.makePost(PostIdUtil.createId(), title);
        postRepository.save(post);

        // 게시글을 파일로 저장. (게시글의 id 와 title 만 txt 파일에 작성하면 됨.)
        fileService.saveToFile(post);


    }

    public void validatePostInterval() {

        List<Post> posts = postRepository.findAll();

        if (posts.isEmpty()) return;
        // 작성된 게시글이 없는 상태에서는 검증할 것도 없으니 바로 return

        // 마지막으로 작성된 게시글 가져온다.
        Post lastPost = posts.get(posts.size() - 1);
        LocalDateTime lastCreated = lastPost.getCreatedAt();

        // 게시글 작성 시간간격 계산
        Duration duration = Duration.between(lastCreated, LocalDateTime.now());

        if (duration.toMinutes() < 3){
            throw new RuntimeException("마지막 게시글 작성 이후 3분 이후에 새 글을 작성할 수 있습니다");
        }

    }

    // 중복 제목이 있는지 검사.
    public void validateDuplicateTitle(String title) {
        if (postRepository.findAll().stream()
                .anyMatch(p -> p.getTitle().equals(title))){
            throw new RuntimeException("중복된 게시글 제목입니다.");
        }

    }

//    // 파일에서 post 읽어오기
//    public Post readPostFromFile(Long id){
//        return filePostRepository.readFile(id);
//    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(Long id) {
        return postRepository.findPostById(id);
    }

    public boolean deletePostById(Long id) {
        Post post = postRepository.findPostById(id);
        // 파일에서 삭제
        fileService.delete(post);
        return postRepository.delete(id);
    }

    // 게시글 수정 기능(게시글 id 와 수정 내용을 주면, 해당 id 를 가진 게시글을 수정)
    public void editPost(Long id , String title){

        validateDuplicateTitle(title);

        Post foundPost = postRepository.findPostById(id);
        foundPost.changeTitle(title);

        // 해당 게시글에 대응하는 파일 내용도 변경하기
        fileService.updateFile(foundPost);
    }

    public Post getPostByTitle(String title) {
        return postRepository.getPostByTitle(title);
    }

    public Post searchPostByCondition(Long id, String title) {
        return postRepository.searchPostByCondition(id, title);
    }
}