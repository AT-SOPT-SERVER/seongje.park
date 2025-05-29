package org.sopt.dto.user;

import jakarta.validation.constraints.NotBlank;

// 회원을 생성할때 필요한 DTO 입니다.
public record UserCreateRequest(

	@NotBlank
	String name,
	@NotBlank
	String email,
	@NotBlank
	String password
) {
}
