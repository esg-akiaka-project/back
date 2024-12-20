package com.haru.doyak.harudoyak.domain.sharedoyak;

import com.haru.doyak.harudoyak.dto.comment.ResCommentDTO;
import com.haru.doyak.harudoyak.dto.sharedoyak.ReqShareDoyakDTO;
import com.haru.doyak.harudoyak.dto.sharedoyak.ResDoyakDTO;
import com.haru.doyak.harudoyak.dto.sharedoyak.ResShareDoyakDTO;
import com.haru.doyak.harudoyak.entity.*;
import com.haru.doyak.harudoyak.exception.CustomException;
import com.haru.doyak.harudoyak.exception.ErrorCode;
import com.haru.doyak.harudoyak.repository.FileRepository;
import com.haru.doyak.harudoyak.repository.LevelRepository;
import com.haru.doyak.harudoyak.repository.MemberRepository;
import com.haru.doyak.harudoyak.repository.ShareDoyakRepository;
import com.haru.doyak.harudoyak.repository.querydsl.CommentRepository;
import com.haru.doyak.harudoyak.repository.querydsl.DoyakRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ShareDoyakService {
    private final ShareDoyakRepository shareDoyakRepository;
    private final MemberRepository memberRepository;
    private final DoyakRepository doyakRepository;
    private final FileRepository fileRepository;
    private final CommentRepository commentRepository;
    private final LevelRepository levelRepository;

    /*
     * 회원의 서로도약 글 모아보기
     * @param : membaerId(Long)
     * */
    public List<ResShareDoyakDTO.ResMemberShareDoyakDYO> getMemberShareDoyakList(Long memberId){
        List<ResShareDoyakDTO.ResMemberShareDoyakDYO> resMemberShareDoyakDYOS = shareDoyakRepository.findMemberShareDoyakAll(memberId).orElseThrow();
        return resMemberShareDoyakDYOS;
    }

    /**
     * 서로도약 삭제
     * @param memberId 회원pk
     * @param shareDoyakId 서로도약pk
     * @return shareDoyakDeleteResult 서로도약 삭제시 삭제되는 row의 총 수
     * @throws EntityNotFoundException 해당 서로도약이 존재하지 않을때 ErrorCode 반환
     */
    @Transactional
    public long setShareDoyakDelete(Long memberId, Long shareDoyakId) {
        try {

            ShareDoyak selectShareDoyak = shareDoyakRepository.findShaereDoyakByMemeberId(memberId, shareDoyakId)
                    .orElseThrow(() -> new CustomException(ErrorCode.SHARE_DOYAK_NOT_FOUND));
            long shareDoyakAuthorId = selectShareDoyak.getMember().getMemberId();

            // 서로도약 삭제시 삭제되는 row의 총 횟수
            long shareDoyakDeleteResult = 0;

            // 서로도약 글의 작성자가 아니라면
            if(shareDoyakAuthorId != memberId) {

                throw new CustomException(ErrorCode.SHARE_DOYAK_NOT_AUTHOR);

            }else {

                // 해당 서로도약 글의 작성자가 맞다면

                // 해당 서로도약의 댓글 목록
                List<ResCommentDTO.ResCommentDetailDTO> comments = commentRepository.findCommentAll(selectShareDoyak.getShareDoyakId()).orElseThrow(() -> new CustomException(ErrorCode.COMMENT_LIST_NOT_FOUND));
                // 해당 서로도약의 도약하기 목록
                List<Doyak> doyaks = doyakRepository.findDoyakAllByShareDoyakId(selectShareDoyak.getShareDoyakId()).orElseThrow(() -> new CustomException(ErrorCode.DOYAK_LIST_NOT_FOUND));
                if(!comments.isEmpty()){
                    for(ResCommentDTO.ResCommentDetailDTO comment : comments){
                        shareDoyakDeleteResult += commentRepository.commentDelete(comment.getCommentId());

                    }
                }
                if(!doyaks.isEmpty()){
                    shareDoyakDeleteResult += doyakRepository.deleteDoyakByShareDoyakId(selectShareDoyak.getShareDoyakId());
                }
                shareDoyakDeleteResult += shareDoyakRepository.shareDoyakDelete(memberId, shareDoyakId);
                shareDoyakDeleteResult += fileRepository.fileDelete(selectShareDoyak.getFile().getFileId());

                return shareDoyakDeleteResult;

            }
        } catch (EntityNotFoundException entityNotFoundException) {
            throw new CustomException(ErrorCode.SHARE_DOYAK_NOT_FOUND);
        }
    }

    /**
     * 서로도약 상세 조회
     * @param memberId 회원pk
     * @param shareDoyakId 서로도약pk
     * @return ResShareDoyakDTO shareDoyakId(서로도약pk), shareContent(서로도약 내용), shareImageUrl(서로도약 이미지파일 url)
     * @throws CustomException 서로도약 작성자와 해당 작성자가 맞지 않을때 ErrorCode 반환
     */
    public ResShareDoyakDTO setShareDoyakDetail(Long memberId, Long shareDoyakId) {
        Member selectMember = memberRepository.findMemberByMemberId(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        ShareDoyak selectShareDoyak = shareDoyakRepository.findShareDoyakByShareDoyakId(shareDoyakId).orElseThrow(() -> new CustomException(ErrorCode.SHARE_DOYAK_NOT_FOUND));
        if(!selectShareDoyak.getMember().getMemberId().equals(selectMember.getMemberId())){
            throw new CustomException(ErrorCode.SHARE_DOYAK_NOT_AUTHOR);
        }

        return ResShareDoyakDTO.builder()
                .shareDoyakId(selectShareDoyak.getShareDoyakId())
                .shareContent(selectShareDoyak.getContent())
                .shareImageUrl(selectShareDoyak.getFile().getFilePathName())
                .build();
    }

    /**
     * 서로도약 수정
     * @param memberId 회원pk
     * @param shareDoyakId 서로도약pk
     * @param reqShareDoyakDTO shareContent(수정된 서로도약 글 내용)
     * @return update된 row 수
     * @throws EntityNotFoundException 해당 서로도약이 존재하지 않을때
     */
    @Transactional
    public long setShareDoyakUpdate(Long memberId, Long shareDoyakId, ReqShareDoyakDTO reqShareDoyakDTO){

        try {

            // 서로도약 작성자가 맞는지
            ShareDoyak selectShareDoyak = shareDoyakRepository.findShaereDoyakByMemeberId(memberId, shareDoyakId)
                                          .orElseThrow(() -> new CustomException(ErrorCode.SHARE_DOYAK_NOT_FOUND));
            long shareDoyakAuthor = selectShareDoyak.getMember().getMemberId();

            // 서로도약 작성자가 해당 회원이 아니라면
            if (shareDoyakAuthor != memberId) {

                throw new CustomException(ErrorCode.SHARE_DOYAK_NOT_AUTHOR);

            } else {

                // 서로도약 작성자가 해당 회원이 맞다면
                return shareDoyakRepository.shareContentUpdate(shareDoyakId, reqShareDoyakDTO);

            }

        } catch (EntityNotFoundException entityNotFoundException) {
            throw new CustomException(ErrorCode.SHARE_DOYAK_NOT_FOUND);
        }
    }

    /**
     * 서로도약 목록
     * @return resDoyakDTOS shareAuthorNickname(서로도약 작성자 닉네임), shareDoyakId(서로도약pk), goalName(서로도약 작성자 목표명),
     *                      shareContent(서로도약 내용), shareImageUrl(서로도약 이미지파일 url), commentCount(댓글 총 수),
     *                      doyakCount(도약 총 수)
     */
    @Transactional
    public List<ResShareDoyakDTO.ResShareDoyakDTOS> getShareDoyakList(){
        try {
            return shareDoyakRepository.findeAll().orElseThrow(() -> new CustomException(ErrorCode.SHARE_DOYAK_LIST_NOT_FOUND));

        } catch (IllegalArgumentException illegalArgumentException){
            throw new CustomException(ErrorCode.SYNTAX_INVALID_FIELD);
        } catch (NullPointerException nullPointerException){
            throw new CustomException(ErrorCode.EMPTY_VALUE);
        } catch (Exception exception){
            throw new CustomException(ErrorCode.SYSTEM_CONNECTION_ERROR);
        }
    }

    /**
     * 도약하기 추가 & 도약하기 취소
     * @param memberId 회원pk
     * @param shareDoyakId 서로도약pk
     * @return resDoyakDTO doyakCount(해당 게시글의 총 도약수)
     */
    @Transactional
    public ResDoyakDTO setDoyakAdd(Long memberId, Long shareDoyakId) {

        // 도약 테이블에 memberId 존재 여부
        boolean isExistsDoyak = doyakRepository.existsByMemberIdAndShareDoyakId(memberId, shareDoyakId);
        ResDoyakDTO resDoyakDTO = new ResDoyakDTO();

        // 회원이 해당 게시글의 도약하기를 했을 경우
        if (isExistsDoyak) {
            doyakRepository.deleteDoyakByMemberIdAndShareDoyakId(memberId, shareDoyakId);
            // 해당 게시글의 총 도약수 select
            Long doyakCount = doyakRepository.findDoyakAllCount(shareDoyakId);
            resDoyakDTO.setDoyakCount(doyakCount);
            log.info("===============해당 게시글의 도약하기를 취소했을 때 총 도약수 {}", resDoyakDTO.getDoyakCount());
        }else{

            // 회원이 해당 서로도약에 도약하기를 안했을 경우
            Member selectMember = memberRepository.findMemberByMemberId(memberId)
                    .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
            ShareDoyak selectShareDoyak = shareDoyakRepository.findShareDoyakByShareDoyakId(shareDoyakId)
                    .orElseThrow(() -> new CustomException(ErrorCode.SHARE_DOYAK_LIST_NOT_FOUND));

            // 회원과 서로도약게시글이 존재 한다면
            if(selectMember.getMemberId() > 0 && selectShareDoyak.getShareDoyakId() > 0){
                Doyak doyak = Doyak.builder()
                        .doyakId(new DoyakId(
                                        selectShareDoyak.getShareDoyakId(),
                                        selectMember.getMemberId()
                                )
                        )
                        .member(selectMember)
                        .shareDoyak(selectShareDoyak)
                        .build();
                doyakRepository.save(doyak);
                // 해당 게시글의 총 도약수 select
                Long doyakCount = doyakRepository.findDoyakAllCount(shareDoyakId);
                resDoyakDTO.setDoyakCount(doyakCount);
                log.info("===============해당 게시글의 총 도약수 {}", resDoyakDTO.getDoyakCount());
            }

        }

        return resDoyakDTO;
    }

    /**
     * 서로도약 작성
     * @param memberId 회원pk
     * @param reqShareDoyakDTO shareContent(서로도약 내용), shareImegeUrl(서로도약 이미지파일 url)
     */
    @Transactional
    public void setShareDoyakAdd(Long memberId, ReqShareDoyakDTO reqShareDoyakDTO){

        try {

            // 회원 존재 여부 확인
            Member selectMember = memberRepository.findMemberByMemberId(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

            // 회원이 존재 한다면
            if(selectMember.getMemberId() != null){

                // 파일 DB insert
                File file = File.builder()
                        .filePathName(reqShareDoyakDTO.getShareImegeUrl())
                        .build();
                fileRepository.save(file);

                // 서로도약 insert
                ShareDoyak shareDoyak = ShareDoyak.builder()
                        .member(selectMember)
                        .content(reqShareDoyakDTO.getShareContent())
                        .file(file)
                        .build();
                shareDoyakRepository.save(shareDoyak);

                // 레벨 update
                Level level = levelRepository.findLevelByMemberId(memberId).orElseThrow(() ->  new CustomException(ErrorCode.LEVEL_NOT_FOUND));
                level.updateWhenPostShareDoyak();
                levelRepository.save(level);

            }else {
                throw new CustomException(ErrorCode.NULL_VALUE);
            }

        } catch (DataIntegrityViolationException dataIntegrityViolationException){
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

    }

}
