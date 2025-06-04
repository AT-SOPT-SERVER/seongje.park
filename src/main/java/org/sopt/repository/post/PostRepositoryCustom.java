package org.sopt.repository.post;

import org.sopt.dto.PostSearchCondition;
import org.sopt.dto.post.PostSimpleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface PostRepositoryCustom {

	// 제목, 작성자 기준 검색 (동적 쿼리) , queryDSL 사용
	Page<PostSimpleResponse>  searchByTitleAndAuthor(Pageable pageable, PostSearchCondition searchCondition);

	// 게시글 전체 조회시, 커서 기반 페이징
	Slice<PostSimpleResponse> findPostsWithCursor(Long cursorId, Pageable pageable);
}
