package org.sopt.dto.post;


import java.util.List;

import org.sopt.domain.enums.Tag;

import jakarta.validation.constraints.Size;

// 게시글 생성 요청에 대한 DTO
public record PostRequest(
	String title,
	String content,
	@Size(max = 2 , message = "태그는 최대 2개까지만 선택 가능합니다.")
	List<Tag> tags)
{

}




