package org.sopt.repository.post;

import org.sopt.dto.PostSearchCondition;
import org.sopt.dto.post.PostSimpleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {

	// 제목, 작성자 기준 검색 (동적 쿼리) , queryDSL 사용
	Page<PostSimpleResponse>  searchByTitleAndAuthor(Pageable pageable, PostSearchCondition searchCondition);

}
