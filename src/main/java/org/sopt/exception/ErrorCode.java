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


    COMMENT_NOT_FOUND(404, "해당 댓글이 존재하지 않습니다."),


    AUTHORIZATION_FAIL(401, "권한 인증이 실패했습니다."),
    INVALID_PASSWORD(401, "잘못된 패스워드 입니다."),
    DUPLICATE_EMAIL(400, "이미 사용 중인 이메일입니다."),

    MISSING_TOKEN(401, "토큰이 제공되지 않았습니다."),
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),
    UNAUTHORIZED_ACCESS(403, "해당 작업을 수행할 권한이 없습니다."),

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