package org.sopt.service;

import org.sopt.domain.Post;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileService {

    // 파일 쓰기
    public void saveToFile(Post post){

        // 파일의 제목 게시글의 제목.
        String fileName = post.getTitle() + ".txt";

        // fileName 기반으로 txt 파일 생성
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))){
            writer.write("ID : " + post.getId());
            writer.newLine();
            writer.write("TITLE : " + post.getTitle());
            writer.newLine();
        } catch(IOException e){
            System.out.println("파일 저장 실패");
        }

    }

    // 파일 수정
    public void updateFile(Post post){
        saveToFile(post);
    }

    // 파일 삭제
    public void delete(Post post){
        File file = new File(post.getTitle() + ".txt");
        if (file.exists()){
            file.delete();
        }
    }
}
