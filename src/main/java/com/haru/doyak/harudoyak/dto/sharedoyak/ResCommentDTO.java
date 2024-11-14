package com.haru.doyak.harudoyak.dto.sharedoyak;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResCommentDTO {

    private Long commentShareDoyakId;
    private Long commentId;
    private Long commentAuthorId;
    private String commentAuthorNickname;
    private String commentContent;

}
