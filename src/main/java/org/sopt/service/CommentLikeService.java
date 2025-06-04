package org.sopt.service;

import static org.sopt.exception.ErrorCode.*;

import org.sopt.domain.Comment;
import org.sopt.domain.Post;
import org.sopt.domain.User;
import org.sopt.domain.like.CommentLike;
import org.sopt.dto.comment.CommentResponse;
import org.sopt.exception.CommentException;
import org.sopt.exception.PostException;
import org.sopt.exception.UserException;
import org.sopt.repository.like.CommentLikeRepository;
import org.sopt.repository.comment.CommentRepository;
import org.sopt.repository.user.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CommentLikeService {

	private final CommentLikeRepository commentLikeRepository;
	private final CommentRepository commentRepository;
	private final UserRepository userRepository;


	@Transactional
	public CommentResponse likeComment(Long commentId, Long userId) {

		// Comment 를 조회해올 때, X 락을 획득.
		// likeComment 가 트랜잭션 끝(commit or rollback) 나기 전까지,
		// 다른 트랜잭션에서 Comment 에 대한 조회, 수정이 불가능.
		Comment comment = commentRepository.findCommentWithLockById(commentId)
			.orElseThrow(() -> new CommentException(COMMENT_NOT_FOUND));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserException(USER_NOT_FOUND));

		//authorize
		authorize(userId, comment);


		// 해당 유저가, 좋아요를 여러번 누를 수 없게 설정하자
		// 해당 유저가 좋아요를 이미 누른 경우라면, count - 1
		// 누르지 않은 경우라면 , count + 1

		//  특정유저가, 특정 댓글에 좋아요를 눌렀는지 확인
		boolean alreadyLiked = commentLikeRepository.existsByCommentAndUser(comment, user);

		if (alreadyLiked) {// // 특정 유저가 특정 댓글에 좋아요를 이미 누른 경우라면
			comment.minusLike();

			CommentLike existingLike = commentLikeRepository.findByCommentAndUser(comment, user)
				.orElseThrow(() -> new CommentException(COMMENT_NOT_FOUND));
			
			commentLikeRepository.delete(existingLike);
		} else{ // 특정 유저가 특정 댓글에 좋아요를 누르지 않았다면,
			comment.addLike();
			// CommentLike 엔티티 만들고 연관관계 세팅해야겠지
			CommentLike like = CommentLike.createCommentLike(user, comment);
			commentLikeRepository.save(like);
		}

		Long postId = comment.getPost().getId();
		evictPostCommentsCache(postId);

		log.info("댓글 좋아요 토글로 캐시 무효화: commentId = {}, postId = {}, liked = {}",
			commentId, postId, !alreadyLiked);

		return CommentResponse.from(comment);
	}

	@CacheEvict(value = "postComments", key = "#postId")
	public void evictPostCommentsCache(Long postId) {
		log.info("댓글 좋아요 변경으로 댓글 목록 캐시 무효화: postId = {}", postId);
	}


	private static void authorize(Long userId, Comment comment) {
		if (!comment.getAuthor().getId().equals(userId)) {
			throw new PostException(UNAUTHORIZED_ACCESS);
		}
	}

}
