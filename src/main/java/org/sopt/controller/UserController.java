package org.sopt.controller;

import org.sopt.dto.UserCreateRequest;
import org.sopt.service.UserService;
import org.sopt.validator.UserValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public ResponseEntity<Void> joinUser(@RequestBody UserCreateRequest userCreateRequest) {

        // 비즈니스로직과 관련 없는 단순 입력값에 대한 검증은 컨트롤러에서 진행합니다.
        // 추후 @Valid 애노테이션 사용하여 리팩토링 예정.

        UserValidator.validateName(userCreateRequest.name());
        // 사용자 이름이 비어 있으면 예외터짐. 비어 있지 않으면 아래로 내려와 회원가입 정상 진행
        userService.join(userCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
