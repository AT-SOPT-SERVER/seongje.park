package org.sopt.dto.post;


import org.sopt.domain.enums.Tag;

// 게시글 생성 요청에 대한 DTO
public record PostRequest(String title, String content, Tag tag){

}




