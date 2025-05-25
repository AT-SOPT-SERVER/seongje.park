package org.sopt.repository;

import static org.sopt.domain.QPost.*;
import static org.sopt.domain.QUser.*;

import java.util.List;

import org.sopt.dto.PostSearchCondition;
import org.sopt.dto.post.PostSimpleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

public class PostRepositoryImpl implements PostRepositoryCustom{

	// EntityManager, JpaQueryFactory 주입
	private final EntityManager em;
	private final JPAQueryFactory queryFactory;

	public PostRepositoryImpl(EntityManager em) {
		this.em = em;
		this.queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public Page<PostSimpleResponse> searchByTitleAndAuthor(Pageable pageable, PostSearchCondition cond) {

		// 페이징을 위한 쿼리
		// 처음에 생성자 projection 대신, field projection 을 썼는데..
		// field Projection 에서는 기본 생성자 + setter 가 필요하나, 레코드는 기본 생성자 , setter 가 없으므로
		// 매핑이 안됐었음... 그래서 생성자 방식으로 전환.

		List<PostSimpleResponse> result = queryFactory
			.select(Projections.constructor(PostSimpleResponse.class,
				post.id,
				post.title,
				post.user.name.as("userName")
			))
			.from(post)
			.leftJoin(post.user, user)
			.where(titleEq(cond.title()),
				authorEq(cond.author()))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// count 쿼리(전체 데이터의 개수를 알야아,
		// 전체페이지 수 계산 등이 가능.
		Long count = queryFactory
			.select(post.count())
			.from(post)
			.leftJoin(post.user, user)
			.where(titleEq(cond.title()),
				authorEq(cond.author()))
			.fetchOne();


		return new PageImpl<>(result, pageable, count);
	}

	private BooleanExpression titleEq(String title) {
		return StringUtils.hasText(title) ? post.title.contains(title) : null;
	}

	private BooleanExpression authorEq(String author) {
		return StringUtils.hasText(author) ? post.user.name.contains(author) : null;
	}
}
