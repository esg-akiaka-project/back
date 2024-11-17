package com.haru.doyak.harudoyak.repository.querydsl;


import com.haru.doyak.harudoyak.dto.comment.ReqCommentDTO;
import com.haru.doyak.harudoyak.dto.comment.ResCommentDTO;
import com.haru.doyak.harudoyak.entity.Comment;

import java.util.List;

public interface CommentCustomRepository {

    List<ResCommentDTO> findMemberCommentAll(Long memberId);

    long commentDelete(Long commentId);

    long commentContentUpdate(Long commentId, ReqCommentDTO reqCommentDTO);

    Comment findCommentByMemberId(Long memberId, Long commentId);

    List<ResCommentDTO.ResCommentDetailDTO> findeCommentAll(Long shareDoyakId);

}
