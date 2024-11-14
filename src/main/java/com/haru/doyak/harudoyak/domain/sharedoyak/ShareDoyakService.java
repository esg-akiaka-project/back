package com.haru.doyak.harudoyak.domain.sharedoyak;

import com.haru.doyak.harudoyak.dto.sharedoyak.*;
import com.haru.doyak.harudoyak.entity.*;
import com.haru.doyak.harudoyak.repository.FileRepository;
import com.haru.doyak.harudoyak.repository.LevelRepository;
import com.haru.doyak.harudoyak.repository.MemberRepository;
import com.haru.doyak.harudoyak.repository.ShareDoyakRepository;
import com.haru.doyak.harudoyak.repository.querydsl.DoyakCustomRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ShareDoyakService {
    private final ShareDoyakRepository shareDoyakRepository;
    private final EntityManager entityManager;
    private final MemberRepository memberRepository;
    private final FileRepository fileRepository;
    private final DoyakCustomRepository doyakCustomRepository;
    private final LevelRepository levelRepository;

    /*
     * 회원의 댓글 모아보기
     * @param : membaerId(Long)
     * */
    public List<ResCommentDTO> getMemberCommentList(Long memberId){
        List<ResCommentDTO> resCommentDTOS = shareDoyakRepository.findMemberCommentAll(memberId);
         return resCommentDTOS;
    }

    /*
     * 회원의 서로도약 글 모아보기
     * @param : membaerId(Long)
     * */
    public List<ResShareDoyakDTO> getMemberShareDoyakList(Long memberId){
        List<ResShareDoyakDTO> resShareDoyakDTOS = shareDoyakRepository.findMemberShareDoyakAll(memberId);
        return resShareDoyakDTOS;
    }
    /*
     * 댓글 삭제
     * @param : memberId(Long), commentId(Long)
     * */
    @Transactional
    public long setCommentDelete(Long memberId, Long commentId){

        // 댓글의 작성자가 맞는지
        long commentAuthorId = 0;
        try{
            Comment selectComment = shareDoyakRepository.findCommentByMemberId(memberId, commentId);
            commentAuthorId = selectComment.getMember().getMemberId();
        } catch (NullPointerException nullPointerException) {
            throw new NullPointerException("해당 댓글의 작성자가 아닙니다.");
        }

        // 댓글의 작성자가 맞다면
        long commentDeleteResult = 0;
        if(commentAuthorId == memberId){
            commentDeleteResult = shareDoyakRepository.commentDelete(commentId);
            return commentDeleteResult;
        }

        // 아니라면
        return 0;

    }

    /*
     * 서로도약 삭제
     * @param : memberId(Long), shareDoyakId(Long)
     * */
    @Transactional
    public long setShareDoyakDelete(Long memberId, Long shareDoyakId) {

        // 서로도약 작성자가 맞는지
        long shareDoyakAuthorId = 0;
        try{

            ShareDoyak selectShareDoyak = shareDoyakRepository.findShaereDoyakByMemeberId(memberId, shareDoyakId);
            shareDoyakAuthorId = selectShareDoyak.getMember().getMemberId();
        }catch (NullPointerException nullPointerException){
            throw new NullPointerException("해당 글의 작성자가 아닙니다.");
        }

        // 해당 서로도약 글의 작성자가 맞다면
        long shareDoyakDeleteResult = 0;
        if(shareDoyakAuthorId == memberId) {
            shareDoyakDeleteResult = shareDoyakRepository.shareDoyakDelete(shareDoyakId);
            return shareDoyakDeleteResult;
        }
        // 아니라면
        return 0;
    }

    /*
     * 댓글 수정
     * @param : memberId(Long), commentId(Long)
     * */
    @Transactional
    public long setCommentUpdate(Long memberId, Long commentId, ReqCommentDTO reqCommentDTO) {

        // 댓글의 작성자가 맞는지
        long commentAuthorId = 0;
        try{
            Comment selectComment = shareDoyakRepository.findCommentByMemberId(memberId, commentId);
            commentAuthorId = selectComment.getMember().getMemberId();
        } catch (NullPointerException nullPointerException) {
            throw new NullPointerException("해당 댓글의 작성자가 아닙니다.");
        }

        // 댓글의 작성자가 맞다면
        long commentUpdateResult = 0;
        if(commentAuthorId == memberId){
            commentUpdateResult = shareDoyakRepository.commentContentUpdate(commentId, reqCommentDTO);
            return commentUpdateResult;
        }
        
        // 아니라면
        return 0;
    }

    /*
     * 서로도약 수정
     * @param : memberId(Long), shareDoyakId(Long), shareContent(String)
     * @return :
     * */
    @Transactional
    public long setShareDoyakUpdate(Long memberId, Long shareDoyakId, ReqShareDoyakDTO reqShareDoyakDTO){

        long shareDoyakAuthor = 0;
        try {
            ShareDoyak selectShareDoyak = shareDoyakRepository.findShaereDoyakByMemeberId(memberId, shareDoyakId);
            shareDoyakAuthor = selectShareDoyak.getMember().getMemberId();
        }catch (NullPointerException nullPointerException){
            throw new NullPointerException("해당 글의 작성자가 아닙니다.");
        }
        // 서로도약 작성자가 해당 회원이 맞다면
        long shareDoyakUpdateResult = 0;
        if(shareDoyakAuthor == memberId){
            shareDoyakUpdateResult = shareDoyakRepository.shareContentUpdate(shareDoyakId, reqShareDoyakDTO);
            return shareDoyakUpdateResult;
        }
        // 아니라면

        return 0;
    }

    /*
     * 댓글 목록
     * @param : shareDoyakId(Long)
     * @return :
     * */
    public List<ResReplyCommentDTO> getCommentList(Long shareDoyakId){
        List<ResReplyCommentDTO> resReplyCommentDTOS = shareDoyakRepository.findeCommentAll(shareDoyakId);
        return resReplyCommentDTOS;
    }

    /*
     * 서로도약 목록
     * @param :
     * @return : List<ResShareDoyakDTO>
     * */
    @Transactional
    public List<ResShareDoyakDTO> getShareDoyakList(){
        List<ResShareDoyakDTO> resDoyakDTOS = shareDoyakRepository.findeAll();
        return resDoyakDTOS;
    }

    /*
     * 댓글 작성
     * @param : memberId(Long), shareDoyakId(Long), commentContent(String)
     * @return :
     * */
    @Transactional
    public void setCommentAdd(Long memberId, Long shareDoyakId, ReqCommentDTO reqCommentDTO){
        // 회원아이디와 서로도약아이디 null체크
        if(memberId == 0 && shareDoyakId == 0){

        }

        // 회원 존재 여부 확인
        boolean isExistsMember = memberRepository.existsByMemberId(memberId);

        // 서로도약 존재 여부 확인
        boolean isExistsShareDoyak = shareDoyakRepository.existsById(shareDoyakId);

        if(isExistsMember && isExistsShareDoyak){
            // 회원 select
            Member selectMember = memberRepository.findMemberByMemberId(memberId);
            // 서로도약 select
            ShareDoyak selectShareDoyak = shareDoyakRepository.findShareDoyakByShareDoyakId(shareDoyakId);


            // 댓글 insert
            Comment comment = Comment.builder()
                    .shareDoyak(selectShareDoyak)
                    .member(selectMember)
                    .content(reqCommentDTO.getCommentContent())
                    .build();
            entityManager.persist(comment);
        }

    }

    /*
     * 도약하기 추가
     * req : memberId(Long), shareDoyakId(Long)
     * res : doyakCount(Long)
     * */
    @Transactional
    public Long setDoyakAdd(Long memberId, Long shareDoyakId) {

        // 도약 테이블에 memberId 존재 여부
        boolean isExistsDoyak = doyakCustomRepository.existsByMemberId(memberId);

        //
        if (isExistsDoyak) {
            log.info("해당 아이디가 있는 도약이 있니?");
            doyakCustomRepository.deleteDoyakByMemberId(memberId);
            Long doyakCount = doyakCustomRepository.findDoyakAllCount();
            return doyakCount;
        }

        // 회원 존재 여부 확인
        boolean isExistsMember = memberRepository.existsByMemberId(memberId);
        boolean isExistsShareDoyak = shareDoyakRepository.existsByshareDoyakId(shareDoyakId);

        // 회원과 서로도약게시글이 존재 한다면
        if(isExistsMember && isExistsShareDoyak){

            Member selectMember = memberRepository.findMemberByMemberId(memberId);
            ShareDoyak selectShareDoyak = shareDoyakRepository.findShareDoyakByShareDoyakId(shareDoyakId);

            log.info("=============================================shareDoyakService");
            log.info("memberId {}", selectMember.getMemberId());
            log.info("shareDoyakId {}", selectShareDoyak.getShareDoyakId());

            Doyak doyak = Doyak.builder()
                    .doyakId(new DoyakId(
                            selectShareDoyak.getShareDoyakId(),
                            selectMember.getMemberId()
                            )
                    )
                    .member(selectMember)
                    .shareDoyak(selectShareDoyak)
                    .build();
            entityManager.persist(doyak);
        }
        // 회원이 존재하지 않다면


        // 해당 게시글의 총 도약수 select
        Long doyakCount = doyakCustomRepository.findDoyakAllCount();
        log.info("===============해당 게시글의 총 도약수 ");
        return doyakCount;

    }

    /*
     * 서로도약 작성
     * @param : memberId(Long), shareContent(String), shareImegeUrl(String), shareOriginalName(String)
     * @return :
     * */
    @Transactional
    public void setShareDoyakAdd(Long memberId, ReqShareDoyakDTO reqShareDoyakDTO){

        // 회원 존재 여부 확인
        boolean isExistsMember = memberRepository.existsByMemberId(memberId);

        // 회원이 존재 한다면
        if(isExistsMember){

            // 파일 DB insert
            File file = File.builder()
                    .filePathName(reqShareDoyakDTO.getShareImegeUrl())
                    .build();
            entityManager.persist(file);

            // persist()는 insert와 동시에 pk값을 조회할 수 있음 .getXXX()
            File selectFile = fileRepository.findByFileId(file.getFileId());
            Member selectMember = memberRepository.findMemberByMemberId(memberId);

            // 서로도약 insert
            ShareDoyak shareDoyak = ShareDoyak.builder()
                    .member(selectMember)
                    .content(reqShareDoyakDTO.getShareContent())
                    .file(selectFile)
                    .build();
            entityManager.persist(shareDoyak);

            // 레벨 update
            Level level = levelRepository.findLevelByMemberId(memberId).orElseThrow();
            level.updateWhenPostShareDoyak();
            levelRepository.save(level);
        }


    }

}
