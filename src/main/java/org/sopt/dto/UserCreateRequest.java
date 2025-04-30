package org.sopt.dto;

public record UserCreateRequest(
        String name,
        String email
) {
}
