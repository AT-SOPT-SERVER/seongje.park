package org.sopt.dto.user;

// 회원을 생성할때 필요한 DTO 입니다.
public record UserCreateRequest(
        String name,
        String email
) {
}
