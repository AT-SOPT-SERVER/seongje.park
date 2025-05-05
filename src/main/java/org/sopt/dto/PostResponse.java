package org.sopt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PostResponse(Long id, String title, String content, String userName){

    public static PostResponse ofTitleAndUser(Long id, String title, String userName){
        return new PostResponse(id, title, null, userName);
    }
}
