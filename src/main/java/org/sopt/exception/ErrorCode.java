package org.sopt.exception;

public enum ErrorCode {

    TITLE_EMPTY(400, "게시글 제목이 비어있습니다."),
    TITLE_TOO_LONG(400, "게시글 제목이 30자를 초과했습니다."),
    DUPLICATE_TITLE(400, "중복된 게시글이 존재합니다."),
    CONTENT_TOO_LONG(400, "게시글 내용이 1,000자를 초과했습니다"),

    CONTENT_EMPTY(400, "게시글 내용이 비어있습니다."),
    POST_NOT_FOUND(404, "해당 게시글이 존재하지 않습니다."),

    USER_NOT_FOUND(404, "해당 회원이 존재하지 않습니다."),
    USER_NAME_EMPTY(400, "회원 이름이 비어있습니다."),
    USER_NAME_TOO_LONG(400, "회원 이름이 10자를 초과했습니다."),


    INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다.");

    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}