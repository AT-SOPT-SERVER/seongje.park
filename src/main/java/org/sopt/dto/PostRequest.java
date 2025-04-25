package org.sopt.dto;


public class PostRequestDto {

    private String title;

    public PostRequestDto() {

    }

    public PostRequestDto(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
