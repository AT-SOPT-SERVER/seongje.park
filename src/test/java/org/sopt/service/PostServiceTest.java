package org.sopt.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.sopt.dto.comment.CommentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
class PostServiceTest {

	@Autowired
	private PostService postService;

	@Autowired
	private AsyncService asyncCommentService;

	@Test
	void 댓글_조회_성능_테스트() throws Exception {
		Long postId = 1L;

		// 동기 방식
		long syncStart = System.currentTimeMillis();
		List<CommentResponse> syncResult = postService.getAllCommentsByPost(postId);
		long syncTime = System.currentTimeMillis() - syncStart;

		// 비동기 방식
		long asyncStart = System.currentTimeMillis();
		List<CommentResponse> asyncResult = postService.getAllCommentsByPost(postId);
		long asyncTime = System.currentTimeMillis() - asyncStart;

		System.out.printf("동기: %dms, 비동기: %dms%n", syncTime, asyncTime);
		assertThat(asyncTime).isLessThanOrEqualTo(syncTime);
	}

	@Test
	void 병렬_좋아요_수_조회_테스트() throws Exception {
		Long postId = 1L;
		List<Long> commentIds = Arrays.asList(1L, 2L, 3L, 4L, 5L);

		// 순차 조회
		long sequentialStart = System.currentTimeMillis();
		List<Long> sequentialResults = commentIds.stream()
			.map(id -> asyncCommentService.getCommentLikeCountAsync(id).join())
			.collect(Collectors.toList());
		long sequentialTime = System.currentTimeMillis() - sequentialStart;

		// 병렬 조회
		long parallelStart = System.currentTimeMillis();
		List<CompletableFuture<Long>> futures = commentIds.stream()
			.map(asyncCommentService::getCommentLikeCountAsync)
			.collect(Collectors.toList());

		CompletableFuture<Void> allOf = CompletableFuture.allOf(
			futures.toArray(new CompletableFuture[0]));

		List<Long> parallelResults = allOf.thenApply(v ->
			futures.stream()
				.map(CompletableFuture::join)
				.collect(Collectors.toList())
		).get();
		long parallelTime = System.currentTimeMillis() - parallelStart;

		System.out.printf("순차: %dms, 병렬: %dms%n", sequentialTime, parallelTime);
		assertThat(parallelResults).isEqualTo(sequentialResults);
		assertThat(parallelTime).isLessThan(sequentialTime);
	}


}