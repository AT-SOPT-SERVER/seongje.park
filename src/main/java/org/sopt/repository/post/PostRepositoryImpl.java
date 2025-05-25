package org.sopt.repository.post;

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
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

public class PostRepositoryImpl implements PostRepositoryCustom {

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
			// .where(titleEq(cond.title()),
			// 	authorEq(cond.author()))
			.where(titleMatch(cond.title()),
				authorMatch(cond.author()))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// count 쿼리(전체 데이터의 개수를 알야아,
		// 전체페이지 수 계산 등이 가능.
		Long count = queryFactory
			.select(post.count())
			.from(post)
			.leftJoin(post.user, user)
			// .where(titleEq(cond.title()),
			// 	authorEq(cond.author()))
			.where(titleMatch(cond.title()),
				authorMatch(cond.author()))
			.fetchOne();


		return new PageImpl<>(result, pageable, count);
	}

	// 흠.. 근데 여기서 고민해봐야 할점이 있는데..
	// 과제의 요구사항은 키워드를 "포함" 하는 것이 기준이었음. 즉, 완전히 일치하지 않아도 된다는 것
	// 그럼 contains 를 써야한다는건데, querydsl 에서는 contains 가 like %검색어% 로 됨.
	// title 과 , author 에 대해 인덱스를 설정해봤자 검색어 앞에 무언가 와버리면
	// 인덱스를 못 타니까 의미가 없어짐.
	// FULL-TEXT-SEARCH 가 있는데. 이걸 적용하는게 querydsl 에서 좀 번거로워 보임...

	// full text index 를 ngram parser 를 통해 만들면 해결이 가능함.
	// 근데 이걸로 성능향상이 될까? 과도한 매칭이 되는 것 같은데
	private BooleanExpression titleEq(String title) {
		return StringUtils.hasText(title) ? post.title.contains(title) : null;
	}

	private BooleanExpression authorEq(String author) {
		return StringUtils.hasText(author) ? post.user.name.contains(author) : null;
	}

	private static final double MATCH_THRESHOLD = 0;

	private BooleanExpression titleMatch(String title) {
		if (!StringUtils.hasText(title)) {
			return null;
		}
		String formatted = "+" + title + "*"; // 필요 시 Boolean 모드 문법에 맞게 가공
		return Expressions.numberTemplate(Double.class,
				"function('match_against', {0}, {1})", post.title, formatted)
			.gt(MATCH_THRESHOLD);
	}

	private BooleanExpression authorMatch(String author) {
		if (!StringUtils.hasText(author)) {
			return null;
		}
		String formatted = "+" + author + "*";;
		return Expressions.numberTemplate(Double.class,
				"function('match_against', {0}, {1})", post.user.name, formatted)
			.gt(MATCH_THRESHOLD);
	}

}
