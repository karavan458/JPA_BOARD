package com.project.spring_jpa_board.domain;

import com.project.spring_jpa_board.domain.entity.Post;
import com.project.spring_jpa_board.web.dto.post.PostUpdateRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class PostTest {

    @Test
    void update() {
        //given
        Post post = Post.builder().title("old title").content("old content").build();
        PostUpdateRequest updateRequest = new PostUpdateRequest("new title", "new content");

        //when
        post.update(updateRequest.getTitle(), updateRequest.getContent());

        //then
        assertThat(post.getTitle()).isEqualTo("new title");
        assertThat(post.getContent()).isEqualTo("new content");
    }

    @Test
    @DisplayName("게시글 수정 : 특정 필드만 수정 했을 때 다른 필드는 초기값 유지")
    void update_Test() {
        //given
        String oldTitle = "old title";
        String oldContent = "old content";
        Post post = Post.builder().title(oldTitle).content(oldContent).build();

        //when
        String newTitle = "new title";
        post.update(newTitle, oldContent);

        //then
        assertThat(post.getTitle()).isEqualTo(newTitle);

        assertThat(post.getContent()).isEqualTo(oldContent);
        assertThat(post.getContent()).isNotNull();
    }
}
