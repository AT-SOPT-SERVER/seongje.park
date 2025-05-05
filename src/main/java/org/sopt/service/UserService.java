package org.sopt.service;


import org.sopt.domain.User;
import org.sopt.dto.UserCreateRequest;
import org.sopt.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 회원가입
    public void join(UserCreateRequest userCreateRequest){


        User user = new User(userCreateRequest.name(), userCreateRequest.email());
        userRepository.save(user);
    }
}
