package org.sopt.service;

import static org.sopt.exception.ErrorCode.*;

import org.sopt.domain.Post;
import org.sopt.domain.User;
import org.sopt.domain.like.PostLike;
import org.sopt.dto.post.PostResponse;
import org.sopt.exception.PostException;
import org.sopt.exception.UserException;
import org.sopt.repository.like.PostLikeRepository;
import org.sopt.repository.post.PostRepository;
import org.sopt.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLikeService {

	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final PostLikeRepository postLikeRepository;

	@Transactional
	public PostResponse likePost(Long postId, Long userId) {

		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostException(POST_NOT_FOUND));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserException(USER_NOT_FOUND));


		// 해당 유저가, 좋아요를 여러번 누를 수 없게 설정하자
		// 해당 유저가 좋아요를 이미 누른 경우라면, count - 1
		// 누르지 않은 경우라면 , count + 1

		// postLikeRepository 에서 특정유저가, 특정게시글에 좋아요를 눌렀는지 확인
		boolean alreadyLiked = postLikeRepository.existsByPostAndUser(post, user);

		if (alreadyLiked) {// count - 1
			post.minusLike();

			PostLike existingLike = postLikeRepository.findByPostAndUser(post, user)
				.orElseThrow(() -> new PostException(POST_NOT_FOUND));

			postLikeRepository.delete(existingLike);

		} else{
			post.addLike(); // post 의 좋아요 수 1 증가
			// PostLike 엔티티 만들고 연관관계 세팅해야겠지
			PostLike like = PostLike.createPostLike(user, post);
			postLikeRepository.save(like);
		}

		return PostResponse.from(post);
	}
}
