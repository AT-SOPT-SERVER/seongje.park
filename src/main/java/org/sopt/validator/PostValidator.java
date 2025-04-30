package org.sopt.validator;

import org.sopt.exception.ErrorCode;
import org.sopt.exception.PostException;
import org.sopt.util.StringControlUtil;

import static org.sopt.exception.ErrorCode.*;

public class PostValidator {

    public static void validateTitle(String title){

        if (title == null || title.isBlank()){
            throw new PostException(TITLE_EMPTY);
        }
        // 제목이 30자를 넘지 않는지 검증 (+추가 기능 : 제목에 이모지를 허용하고, 이모지를 1글자가 되도록)
        if (StringControlUtil.countCharacters(title) > 30) {
            throw new PostException(TITLE_TOO_LONG);
        }
    }

    public static void validateContent(String content) {

        // 게시글 내용은 비어 있으면 안 된다.
        if (content == null || content.isBlank()) {
            throw new PostException(TITLE_EMPTY);
        }

        // 게시글 내용은 1000자를 넘으면 안 된다.
        if (content.length() > 1000){

            throw new PostException(CONTENT_TOO_LONG);
        }
    }


}
