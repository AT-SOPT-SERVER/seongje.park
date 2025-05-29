package org.sopt.dto.login;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
	@NotBlank(message = "이메일이 비어있으면 안 됩니다.")
	String email,
	@NotBlank(message = "패스워드가 비어있으면 안 됩니다.")
	String password
) {
}
