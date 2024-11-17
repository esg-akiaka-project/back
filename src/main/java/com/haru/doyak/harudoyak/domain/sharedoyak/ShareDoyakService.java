package com.haru.doyak.harudoyak.domain.sharedoyak;

import com.haru.doyak.harudoyak.dto.sharedoyak.*;
import com.haru.doyak.harudoyak.entity.*;
import com.haru.doyak.harudoyak.exception.CustomException;
import com.haru.doyak.harudoyak.exception.ErrorCode;
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
    private final DoyakCustomRepository doyakCustomRepository;
    private final FileRepository fileRepository;
    private final LevelRepository levelRepository;

    /*
     * 회원의 서로도약 글 모아보기
     * @param : membaerId(Long)
     * */
    public List<ResShareDoyakDTO> getMemberShareDoyakList(Long memberId){
        List<ResShareDoyakDTO> resShareDoyakDTOS = shareDoyakRepository.findMemberShareDoyakAll(memberId);
        return resShareDoyakDTOS;
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
     * 도약하기 추가
     * req : memberId(Long), shareDoyakId(Long)
     * res : doyakCount(Long)
     * */
    @Transactional
    public ResDoyakDTO setDoyakAdd(Long memberId, Long shareDoyakId) {

        // 도약 테이블에 memberId 존재 여부
        boolean isExistsDoyak = doyakCustomRepository.existsByMemberIdAndShareDoyakId(memberId, shareDoyakId);
        ResDoyakDTO resDoyakDTO = new ResDoyakDTO();
        //
        if (isExistsDoyak) {
            doyakCustomRepository.deleteDoyak(memberId, shareDoyakId);
            Long doyakCount = doyakCustomRepository.findDoyakAllCount(shareDoyakId);
            resDoyakDTO.setDoyakCount(doyakCount);
            return resDoyakDTO;
        }

        // 회원 존재 여부 확인
        boolean isExistsMember = memberRepository.existsByMemberId(memberId);
        boolean isExistsShareDoyak = shareDoyakRepository.existsByshareDoyakId(shareDoyakId);

        // 회원과 서로도약게시글이 존재 한다면
        if(isExistsMember && isExistsShareDoyak){

            Member selectMember = memberRepository.findMemberByMemberId(memberId);
            ShareDoyak selectShareDoyak = shareDoyakRepository.findShareDoyakByShareDoyakId(shareDoyakId);

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
        if(!isExistsMember){
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }else if(!isExistsShareDoyak){
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }

        // 해당 게시글의 총 도약수 select
        Long doyakCount = doyakCustomRepository.findDoyakAllCount(shareDoyakId);
        resDoyakDTO.setDoyakCount(doyakCount);
        log.info("===============해당 게시글의 총 도약수 {}", resDoyakDTO.getDoyakCount());
        return resDoyakDTO;

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
