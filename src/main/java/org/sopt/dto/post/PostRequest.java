package org.sopt.dto.post;

import java.util.List;

import org.sopt.domain.enums.Tag;
import org.sopt.validator.annotation.ValidTitle;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// 게시글 생성 요청에 대한 DTO
public record PostRequest(

	@NotBlank(message = "게시글 제목은 비어있을 수 없습니다.")
	@ValidTitle // 이모티콘 처리를 위한 커스텀 애노테이션
	String title,

	@NotBlank(message = " 게시글 내용은 비어있을 수 없습니다.")
	@Size(max = 1000, message = "게시글 내용은 1000자를 넘을 수 없습니다.")
	String content,

	@Size(max = 2, message = "태그는 최대 2개까지만 선택 가능합니다.")
	List<Tag> tags) {

}




