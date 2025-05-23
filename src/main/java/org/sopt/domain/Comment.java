package org.sopt.domain;

import java.util.ArrayList;
import java.util.List;

import org.sopt.common.entity.BaseEntity;
import org.sopt.domain.like.CommentLike;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Comment extends BaseEntity {

	@Id @GeneratedValue
	private Long id;

	private String content; // 댓글 내용

	// 누가 댓글을 작성했는지
	@ManyToOne(fetch = FetchType.LAZY) // 연관관계의 주인
	@JoinColumn(name = "USER_ID")
	private User author; // 댓글 작성자

	// 어떤 게시글 소속인지
	@ManyToOne(fetch = FetchType.LAZY) // 연관관계의 주인
	@JoinColumn(name = "POST_ID")
	private Post post;

	// 댓글의 좋아요 수
	private Integer likeCount = 0;

	// 댓글의 좋아요 정보 리스트
	@OneToMany(mappedBy = "comment", fetch = FetchType.LAZY, orphanRemoval = true)
	private List<CommentLike> likes = new ArrayList<>();

	// 댓글의 좋아요 수 증가
	public void addLike(){
		this.likeCount += 1;
	}
	// 댓글의 좋아요 수 감소
	public void minusLike(){
		this.likeCount -= 1;
	}

	@Builder
	public Comment(String content, User author , Post post) {
		this.content = content;
		this.author = author;
		this.post = post;
		
		post.getComments().add(this); // 컬렉션에 추가함으로써 POST- COMMENT 간 데이터 정확성 만족
		author.getComments().add(this); // 컬렉션에 추가함으로써 USER- COMMENT 간 데이터 정확성 만족 
	}


	public static Comment createComment(String content, User author, Post post) {
		return Comment.builder()
			.content(content)
			.author(author)
			.post(post)
			.build();
	}


	public void updateContent(String content) {
		this.content = content;
	}




}
