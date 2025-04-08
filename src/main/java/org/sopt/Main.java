package org.sopt;

import java.util.Scanner;
import org.sopt.controller.PostController;
import org.sopt.domain.Post;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PostController controller = new PostController();

        printWelcome();

        while (true) {
            printMenu();

            System.out.print("👉 선택: ");
            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    System.out.println("\n📝 [게시글 작성]");
                    System.out.print("📌 제목을 입력해주세요: ");
                    String title = scanner.nextLine();
                    controller.createPost(title);
                    System.out.println("✅ 게시글이 성공적으로 저장되었습니다!");
                    break;

                case "2":
                    System.out.println("\n📚 [전체 게시글 조회]");
                    for (Post post : controller.getAllPosts()) {
                        System.out.printf("🆔 %d | 📌 제목: %s\n", post.getId(), post.getTitle());
                    }
                    break;

                case "3":
                    System.out.println("\n🔍 [게시글 상세 조회]");
                    System.out.print("📌 조회할 게시글 ID를 입력해주세요: ");
                    long id = Integer.parseInt(scanner.nextLine());
                    Post found = controller.getPostById(id);
                    if (found != null) {
                        System.out.println("📄 게시글 상세 내용:");
                        System.out.println("-------------------------------------");
                        System.out.printf("🆔 ID: %d\n", found.getId());
                        System.out.printf("📌 제목: %s\n", found.getTitle());
                        System.out.println("-------------------------------------");
                    } else {
                        System.out.println("❌ 해당 ID의 게시글이 존재하지 않습니다.");
                    }
                    break;

                case "4":
                    System.out.println("\n🗑️ [게시글 삭제]");
                    System.out.print("📌 삭제할 게시글 ID를 입력해주세요: ");
                    long deleteId = Integer.parseInt(scanner.nextLine());
                    boolean deleted = controller.deletePostById(deleteId);
                    if (deleted) {
                        System.out.println("🗑️ 게시글이 성공적으로 삭제되었습니다.");
                    } else {
                        System.out.println("❌ 삭제할 게시글이 존재하지 않습니다.");
                    }
                    break;
                case "5":
                    System.out.println("게시글 검색 기능");
                    System.out.println("검색할 게시글의 ID 또는 제목을 입력해주세요 : ");

                    Long postId = null;
                    String postTitle = null;

                    String postIdInput = scanner.nextLine();
                    if (!postIdInput.isBlank()) postId = Long.parseLong(postIdInput);

                    String postTitleInput = scanner.nextLine();
                    if (!postTitleInput.isBlank()) postTitle = postTitleInput;

                    Post foundPost = controller.searchPostByCondition(postId, postTitle);
                    if (foundPost != null) {
                        System.out.println("📄 게시글 상세 내용:");
                        System.out.println("-------------------------------------");
                        System.out.printf("🆔 ID: %d\n", foundPost.getId());
                        System.out.printf("📌 제목: %s\n", foundPost.getTitle());
                        System.out.println("-------------------------------------");
                    } else {
                        System.out.println("게시글이 존재하지 않습니다.");
                    }
                    break;

                case "6":
                    System.out.println("게시글 제목으로 조회");
                    String titleInput = scanner.nextLine();
                    Post postByTitle = controller.getPostByTitle(titleInput);
                    if (postByTitle != null) {
                        System.out.println("📄 게시글 상세 내용:");
                        System.out.println("-------------------------------------");
                        System.out.printf("🆔 ID: %d\n", postByTitle.getId());
                        System.out.printf("📌 제목: %s\n", postByTitle.getTitle());
                        System.out.println("-------------------------------------");
                    } else {
                        System.out.println("게시글이 존재하지 않습니다.");
                    }


                case "0":
                    System.out.println("\n👋 프로그램을 종료합니다. 감사합니다!");
                    return;

                default:
                    System.out.println("⚠️ 잘못된 입력입니다. 다시 시도해주세요.");
            }
        }
    }

    private static void printWelcome() {
        System.out.println("=====================================");
        System.out.println("📌  자바 게시판 프로그램에 오신 것을 환영합니다!");
        System.out.println("=====================================\n");
    }

    private static void printMenu() {
        System.out.println("\n================ 메뉴 ================");
        System.out.println("1️⃣  게시글 작성");
        System.out.println("2️⃣  전체 게시글 조회");
        System.out.println("3️⃣  게시글 상세 조회");
        System.out.println("4️⃣  게시글 삭제");
        System.out.println("0️⃣  프로그램 종료");
        System.out.println("=====================================");
    }
}
