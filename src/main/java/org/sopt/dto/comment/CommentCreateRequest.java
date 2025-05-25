package org.sopt.dto.comment;

import jakarta.validation.constraints.Size;

public record CommentCreateRequest(
	@Size(max = 300, message = "댓글은 최대 300자까지 작성 가능합니다.")
	String content

	// 문자열 제한 할때는 Size
	// 숫자 제한 할때는 Max

) {

}
