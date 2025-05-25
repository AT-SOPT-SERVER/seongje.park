package org.sopt.service;

import static org.sopt.exception.ErrorCode.*;

import org.sopt.domain.Comment;
import org.sopt.domain.User;
import org.sopt.domain.like.CommentLike;
import org.sopt.dto.comment.CommentResponse;
import org.sopt.exception.CommentException;
import org.sopt.exception.UserException;
import org.sopt.repository.like.CommentLikeRepository;
import org.sopt.repository.comment.CommentRepository;
import org.sopt.repository.user.UserRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

	private final CommentLikeRepository commentLikeRepository;
	private final CommentRepository commentRepository;
	private final UserRepository userRepository;


	public CommentResponse likeComment(Long commentId, Long userId) {

		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new CommentException(COMMENT_NOT_FOUND));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserException(USER_NOT_FOUND));


		// 해당 유저가, 좋아요를 여러번 누를 수 없게 설정하자
		// 해당 유저가 좋아요를 이미 누른 경우라면, count - 1
		// 누르지 않은 경우라면 , count + 1

		//  특정유저가, 특정 댓글에 좋아요를 눌렀는지 확인
		boolean alreadyLiked = commentLikeRepository.existsByCommentAndUser(comment, user);

		if (alreadyLiked) {// // 특정 유저가 특정 댓글에 좋아요를 이미 누른 경우라면
			comment.minusLike();
		} else{ // 특정 유저가 특정 댓글에 좋아요를 누르지 않았다면,
			comment.addLike();
			// CommentLike 엔티티 만들고 연관관계 세팅해야겠지
			CommentLike like = CommentLike.createCommentLike(user, comment);
			commentLikeRepository.save(like);
		}

		return CommentResponse.from(comment);
	}

}
