package org.sopt.service;

import org.sopt.domain.Post;
import org.sopt.repository.PostRepository;

import java.util.List;

public class PostService {

    private PostRepository postRepository = new PostRepository();

    public void createPost(Post post) {
        postRepository.save(post);
    }

    public List<Post> getAllPosts(){
        return postRepository.findAll();
    }

    // 게시글 id로 조회
    public Post getPostById(int id){
       return  postRepository.findById(id);
    }


    // 해당 id 를 가진 게시글 삭제
    public boolean deletePostById(int id) {
        return postRepository.deleteById(id);
    }
}
