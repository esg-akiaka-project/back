package com.haru.doyak.harudoyak.repository.querydsl;

import com.haru.doyak.harudoyak.dto.sharedoyak.*;
import com.haru.doyak.harudoyak.entity.Comment;
import com.haru.doyak.harudoyak.entity.ShareDoyak;

import java.util.List;

public interface ShareDoyakCustomRepository {

    List<ResCommentDTO> findMemberCommentAll(Long memberId);

    List<ResShareDoyakDTO> findMemberShareDoyakAll(Long memberId);

    long commentDelete(Long commentId);

    long shareDoyakDelete(Long shareDoyakId);

    long commentContentUpdate(Long commentId, ReqCommentDTO reqCommentDTO);

    Comment findCommentByMemberId(Long memberId, Long commentId);

    ShareDoyak findShaereDoyakByMemeberId(Long memeberId, Long shareDoyakId);

    List<ResShareDoyakDTO> findeAll();

    List<ResReplyCommentDTO> findeCommentAll(Long shareDoyakId);

    long shareContentUpdate(Long shareDoyakId, ReqShareDoyakDTO reqShareDoyakDTO);

}
