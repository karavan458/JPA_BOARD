package com.project.spring_jpa_board.domain;

import com.project.spring_jpa_board.domain.entity.Comment;
import com.project.spring_jpa_board.domain.entity.CommentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentTest {

    @Test
    @DisplayName("댓글 삭제 상태 : 삭제된 댓글은 전용 문구 반환")
    void commentStatus() {
        //given
        Comment comment = Comment.builder().content("살아있는 댓글").build();

        //when
        comment.delete();

        //then
        assertThat(comment.getStatus()).isEqualTo(CommentStatus.DELETED);
        assertThat(comment.getDisplayContent()).isEqualTo("삭제된 댓글입니다.");
    }

    @Test
    @DisplayName("연관관계 디버깅 : 부모 댓글에 자식을 추가하면 양방향 참조가 가능하다.")
    void commentRelationTest() {
        //given
        Comment parent = Comment.builder().content("부모 댓글").build();
        Comment child = Comment.builder().content("자식 댓글").build();

        assertThat(parent.getStatus()).isEqualTo(CommentStatus.NORMAL);
        assertThat(parent.getChildren()).isEmpty();

        //when
        parent.addChildComment(child);

        //then
        assertThat(parent.getChildren()).contains(child);
        assertThat(parent.getChildren()).hasSize(1);

        assertThat(child.getParent()).isEqualTo(parent);
    }
}
