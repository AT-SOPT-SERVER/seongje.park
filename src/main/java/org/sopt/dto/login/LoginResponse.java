package org.sopt.dto.login;

public record LoginResponse(
	String accessToken,
	Long userId,
	String email,
	String name
) {

	public static LoginResponse of(String accessToken, Long userId,
		String email, String name){
		return new LoginResponse(accessToken, userId, email, name);
	}
}
