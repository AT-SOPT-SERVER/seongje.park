package org.sopt.exception;

import lombok.Getter;

@Getter
public class PostException extends RuntimeException {

    private final ErrorCode errorCode;

    public PostException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }


}