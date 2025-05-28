package org.sopt.dto.post;

import java.time.LocalDateTime;

import org.sopt.domain.Post;

// 게시글에 대한 간단한 정보 표시할때 응답하는 DTO
public record PostSimpleResponse(
    Long id,
    String title,
    String userName,
    LocalDateTime createdAt
) {

  public static PostSimpleResponse from(Post p){
    return new PostSimpleResponse(
        p.getId(),
        p.getTitle(),
        p.getUser().getName(),
        p.getCreatedAt());
  }

}
