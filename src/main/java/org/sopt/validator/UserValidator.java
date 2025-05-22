package org.sopt.validator;

import org.sopt.exception.ErrorCode;
import org.sopt.exception.UserException;

import static org.sopt.exception.ErrorCode.*;

public class UserValidator {

    public static void validateName(String userName){

        if (userName == null || userName.isBlank()) {
            throw new UserException(USER_NAME_EMPTY);
        }

        // 회원의 이름은 10자를 넘으면 안 된다.
        if (userName.length() > 10){
            throw new UserException(USER_NAME_TOO_LONG);
        }
    }
}
