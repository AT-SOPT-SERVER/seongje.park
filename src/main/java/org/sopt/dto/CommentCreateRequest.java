package org.sopt.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

public record CommentCreateRequest(
	@Size(max = 300)
	String content

	// 문자열 제한 할때는 Size
	// 숫자 제한 할때는 Max

) {

}
