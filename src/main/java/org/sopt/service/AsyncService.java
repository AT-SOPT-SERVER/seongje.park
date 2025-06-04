package org.sopt.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.sopt.domain.Comment;
import org.sopt.dto.comment.CommentResponse;
import org.sopt.repository.comment.CommentRepository;
import org.sopt.repository.like.CommentLikeRepository;
import org.sopt.repository.like.PostLikeRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AsyncService {

	private final CommentRepository commentRepository; // 댓글 저장소
	private final PostLikeRepository postLikeRepository; // 게시글 좋아요 저장소
	private final CommentLikeRepository commentLikeRepository; // 댓글 좋아요 저장소

	// 비동기 댓글 조회
	@Async("asyncExecutor")
	public CompletableFuture<List<Comment>> getCommentsByPostIdAsync(Long postId){
		List<Comment> comments = commentRepository.findCommentsByPostId(postId);

		return CompletableFuture.completedFuture(comments);
	}

	// 비동기 게시글 좋아요 수 조회
	@Async("asyncExecutor")
	public CompletableFuture<Long> getPostLikeCountAsync(Long postId){
		long count = postLikeRepository.countByPostId(postId);

		return CompletableFuture.completedFuture(count);
	}

	// 비동기 댓글 좋아요 수 조회
	@Async("asyncExecutor")
	public CompletableFuture<Long> getCommentLikeCountAsync(Long commentId){
		long count = commentLikeRepository.countByCommentId(commentId);

		return CompletableFuture.completedFuture(count);
	}

	// 댓글과 좋아요 수를 비동기로 함께 조회
	@Async("asyncExecutor")
	public CompletableFuture<List<CommentResponse>> getCommentsWithLikeCountAsync(Long postId){

		List<Comment> comments = commentRepository.findCommentsByPostId(postId);

		List<CompletableFuture<CommentResponse>> commentFutures = comments.stream()
			.map(comment -> getCommentLikeCountAsync(comment.getId())
				.thenApply(likeCount -> new CommentResponse(
					comment.getId(),
					comment.getAuthor().getName(),
					comment.getContent(),
					comment.getCreatedAt(),
					likeCount.intValue())
				))
			.toList();

		CompletableFuture<Void> allOf = CompletableFuture.allOf(
			commentFutures.toArray(new CompletableFuture[0]));

		return allOf.thenApply(v -> commentFutures.stream()
			.map(CompletableFuture::join)
			.collect(Collectors.toList()));

	}







}
