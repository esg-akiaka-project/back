package com.haru.doyak.harudoyak.dto.comment;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResCommentDTO {

    private Long commentShareDoyakId;     // 댓글의 서로도약ID
    private Long commentId;               // 댓글ID
    private String commentAuthorNickname; // 댓글 작성자 닉네임

    @Getter
    @Setter
    public static class ResCommentDetailDTO {

        private Long commentShareDoyakId;        // 댓글의 서로도약ID
        private Long commentId;                  // 댓글ID
        private String commentContent;           // 댓글의 글내용
        private String commentAuthorNickname;    // 댓글 작성자 닉네임
        private Long replyCommentCount;          // 댓글의 답글 총 수
        private List<ResReplyDetailDTO> replies; // 댓글

    }

    @Getter
    @Setter
    public static class ResReplyDetailDTO {

        private Long commentShareDoyakId;     // 댓글의 서로도약ID
        private Long replyId;               // 댓글ID
        private String replyContent;        // 댓글의 글내용
        private String replyAuthorNickname; // 댓글 작성자 닉네임
        private Long parentCommentId;         // 상위 댓글 commentId

    }

}
