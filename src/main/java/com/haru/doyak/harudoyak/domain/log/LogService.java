package com.haru.doyak.harudoyak.domain.log;

import com.haru.doyak.harudoyak.dto.letter.ReqLetterDTO;
import com.haru.doyak.harudoyak.dto.log.ReqLogDTO;
import com.haru.doyak.harudoyak.dto.log.ResLogDTO;
import com.haru.doyak.harudoyak.dto.log.ResDailyLogDTO;
import com.haru.doyak.harudoyak.dto.log.TagDTO;
import com.haru.doyak.harudoyak.entity.*;
import com.haru.doyak.harudoyak.repository.FileRepository;
import com.haru.doyak.harudoyak.repository.LevelRepository;
import com.haru.doyak.harudoyak.repository.LogRepository;
import com.haru.doyak.harudoyak.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogService {
    private final LogRepository logRepository;
    private final EntityManager entityManager;
    private final MemberRepository memberRepository;
    private final FileRepository fileRepository;
    private final LevelRepository levelRepository;

    /*
     * 일간 도약기록 조회
     * @param : memberId(Long), logId(Long)
     * */
    public List<ResDailyLogDTO> getDailyLogDetail(Long memberId, Long logId) {

        List<ResDailyLogDTO> resDailyLogDTOS = logRepository.findLogByLogIdAndMemberId(memberId, logId);

        return resDailyLogDTOS;
    }

    /*
     * 도약이 편지 작성
     * @param : memberId(Long), logId(Long), letterContent(String)
     * @return :
     * */
    @Transactional
    public void setLetterAdd(Long memberId, Long LogId, ReqLetterDTO reqLetterDTO) {
        // 회원 존재 여부
        boolean isExistsMember = memberRepository.existsByMemberId(memberId);

        // 도약 기록 존재 여부
        boolean isExistsLog = logRepository.existsByLogId(LogId);

        // 회원과 도약기록이 존재한다면
        if(isExistsMember && isExistsLog) {
            Log selectLog = logRepository.findLogByLogId(LogId);

            Letter letter = Letter.builder()
                    .log(selectLog)
                    .content(reqLetterDTO.getLetterContent())
                    .build();
            entityManager.persist(letter);
        }
    }

    /*
     * 도약 기록 목록
     * req : memberId(Long)
     * res : logId(Long), creationDate(Date)
     * */
    public List<ResLogDTO> getLogList(Long memberId){

        List<ResLogDTO> resLogDTOS = logRepository.findLogAllByMemberId(memberId);

        return resLogDTOS;
    }

    /*
     * 도약 기록 작성 (에러 처리 해야함)
     * req : memberId(Long),logContent(String), tagName(String []), emotion(String)
     * res : 200 ok 400 등
     * */
    @Transactional
    public ResLogDTO setLogAdd(ReqLogDTO reqLogDTO, Long memberId) {

        // 도약기록 insert 전 회원 존재하는지 isExists 확인
         boolean isExistsMember = memberRepository.existsByMemberId(memberId);

         ResLogDTO resLogDTO = new ResLogDTO();
         // 회원이 존재한다면
         if (isExistsMember){

             // 이미지파일url insert
             File file = File.builder()
                     .filePathName(reqLogDTO.getLogImageUrl())
                     .build();
             entityManager.persist(file);

             File selectFile = fileRepository.findByFileId(file.getFileId());
             Member selectByMember = memberRepository.findMemberByMemberId(memberId);

             // 도약기록 insert
             Log log = Log.builder()
                     .member(selectByMember)
                     .file(selectFile)
                     .content(reqLogDTO.getLogContent())
                     .emotion(reqLogDTO.getEmotion())
                     .build();
             entityManager.persist(log);

             // 태그 insert
             for(TagDTO tagDTO : reqLogDTO.getTagNameList()){
                 Tag tag = Tag.builder()
                         .name(tagDTO.getTagName())
                         .build();
                 entityManager.persist(tag);
                 setLogTag(log, tag);
             }

             // 레벨 update
             Level level = levelRepository.findLevelByMemberId(memberId).orElseThrow();
             level.updateWhenPostLog();
             levelRepository.save(level);

             resLogDTO.setLogId(log.getLogId());
             resLogDTO.setMemberId(selectByMember.getMemberId());
             resLogDTO.setLogContent("기록도약 게시글 작성을 완료했습니다.");
         }

         // 회원이 존재하지 않다면
        return resLogDTO;
    }

    /*
    * 로그&태그 저장
    * req : log(Long), tag(Long)
    * */
    @Transactional
    public void setLogTag(Log log, Tag tag){
        LogTag logTag = LogTag.builder()
                .logTagId(new LogTagId(log.getLogId(), tag.getTagId()))
                .log(log)
                .tag(tag)
                .build();
        entityManager.persist(logTag);
    }

}
