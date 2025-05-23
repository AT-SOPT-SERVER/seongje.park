package org.sopt.dto;

import org.sopt.domain.Post;

public record PostSimpleResponse(
    Long id,
    String title,
    String userName
) {

  public static PostSimpleResponse from(Post p){
    return new PostSimpleResponse(p.getId(), p.getTitle(), p.getUser().getName());
  }



}
