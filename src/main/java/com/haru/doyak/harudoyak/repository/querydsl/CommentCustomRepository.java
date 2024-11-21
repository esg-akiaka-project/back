package com.haru.doyak.harudoyak.repository.querydsl;


import com.haru.doyak.harudoyak.dto.comment.ReqCommentDTO;
import com.haru.doyak.harudoyak.dto.comment.ResCommentDTO;
import com.haru.doyak.harudoyak.entity.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentCustomRepository {

    Optional<List<ResCommentDTO>> findMemberCommentAll(Long memberId);

    long commentDelete(Long commentId);

    long commentContentUpdate(Long commentId, ReqCommentDTO reqCommentDTO);

    Optional<Comment> findCommentByMemberId(Long memberId, Long commentId);

    Optional<List<ResCommentDTO.ResCommentDetailDTO>> findeCommentAll(Long shareDoyakId);

    Optional<Comment> findCommentByCommentId (Long commentId);

}
