package org.sopt.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.dto.comment.CommentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Transactional
@Slf4j
class CachePerformTest {

	@Autowired
	private PostService postService;

	@Autowired
	private CacheManager cacheManager;

	@Test
	@DisplayName("댓글 캐시 성능 측정만")
	void 댓글_캐시_성능_측정() {
		Long postId = 1L;

		System.out.println("=== 캐시 성능 측정 시작 ===");

		// 캐시 초기화
		cacheManager.getCache("postComments").clear();
		System.out.println("캐시 초기화 완료");

		// 1. 첫 번째 조회 (DB에서 조회)
		long dbStart = System.currentTimeMillis();
		List<CommentResponse> dbResult = postService.getAllCommentsByPost(postId);
		long dbTime = System.currentTimeMillis() - dbStart;
		System.out.println("DB 조회: " + dbTime + "ms, 댓글 수: " + dbResult.size());

		// 2. 두 번째 조회 (캐시에서 조회)
		long cacheStart = System.currentTimeMillis();
		List<CommentResponse> cacheResult = postService.getAllCommentsByPost(postId);
		long cacheTime = System.currentTimeMillis() - cacheStart;
		System.out.println("캐시 조회: " + cacheTime + "ms, 댓글 수: " + cacheResult.size());

		// 3. 성능 분석
		if (cacheTime > 0) {
			double improvement = (double) dbTime / cacheTime;
			System.out.println("성능 향상: " + String.format("%.1f", improvement) + "배");
		} else {
			System.out.println("캐시 조회가 너무 빨라서 측정 불가 (< 1ms)");
		}
		System.out.println("시간 절약: " + (dbTime - cacheTime) + "ms");

		// 4. 추가 캐시 조회 테스트 (여러 번)
		System.out.println("\n=== 연속 캐시 조회 테스트 ===");
		for (int i = 1; i <= 5; i++) {
			long start = System.currentTimeMillis();
			List<CommentResponse> result = postService.getAllCommentsByPost(postId);
			long time = System.currentTimeMillis() - start;
			System.out.println(i + "번째 캐시 조회: " + time + "ms");
		}

		// 5. 기본 검증 (개수만)
		assertThat(dbResult).isNotEmpty();
		assertThat(cacheResult).hasSize(dbResult.size());

		System.out.println("=== 테스트 완료 ===");
	}

	@Test
	@DisplayName("캐시 무효화 테스트")
	void 캐시_무효화_테스트() {
		Long postId = 1L;

		System.out.println("=== 캐시 무효화 테스트 ===");

		// 1. 캐시 생성
		cacheManager.getCache("postComments").clear();
		long start1 = System.currentTimeMillis();
		List<CommentResponse> result1 = postService.getAllCommentsByPost(postId);
		long time1 = System.currentTimeMillis() - start1;
		System.out.println("캐시 생성: " + time1 + "ms");

		// 2. 캐시 조회
		long start2 = System.currentTimeMillis();
		List<CommentResponse> result2 = postService.getAllCommentsByPost(postId);
		long time2 = System.currentTimeMillis() - start2;
		System.out.println("캐시 조회: " + time2 + "ms");

		// 3. 캐시 무효화
		postService.evictPostCommentsCache(postId);
		System.out.println("캐시 무효화 실행");

		// 4. 무효화 후 조회
		long start3 = System.currentTimeMillis();
		List<CommentResponse> result3 = postService.getAllCommentsByPost(postId);
		long time3 = System.currentTimeMillis() - start3;
		System.out.println("무효화 후 조회: " + time3 + "ms");

		// 5. 결과 분석
		System.out.println("\n=== 결과 분석 ===");
		System.out.println("캐시 효과: " + (time1 > time2 ? "성공" : "실패"));
		System.out.println("무효화 효과: " + (time3 > time2 ? "성공" : "실패"));

		// 기본 검증
		assertThat(result1).hasSize(result2.size());
		assertThat(result2).hasSize(result3.size());
	}

}