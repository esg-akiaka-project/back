package com.haru.doyak.harudoyak.domain.log;

import com.haru.doyak.harudoyak.dto.letter.ReqLetterDTO;
import com.haru.doyak.harudoyak.dto.log.*;
import com.haru.doyak.harudoyak.entity.*;
import com.haru.doyak.harudoyak.repository.FileRepository;
import com.haru.doyak.harudoyak.repository.LevelRepository;
import com.haru.doyak.harudoyak.repository.LogRepository;
import com.haru.doyak.harudoyak.repository.MemberRepository;
import com.haru.doyak.harudoyak.util.DateUtil;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
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
    private final DateUtil dateUtil;

    /*
     * 월간 도약기록 조회
     * @param : memberId(Long), creationDate(LocalDateTime)
     * */
    public ResLogDTO.ResMontlyLogDTO getMontlyLogDetail(Long memberId, String creationDate) {

        // String 문자열 LocalDateTime으로 변환
        Date resultDate = null;
        try {
            resultDate = dateUtil.stringToLocalDateTime(creationDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        List<ResLetterDTO.LetterMontlyDTO> letterMontlyDTOS = logRepository.findMontlyLetterAll(memberId, resultDate);
        List<EmotionDTO> emotionDTOS = logRepository.findMontlyEmotion(memberId, resultDate);
        List<ResTagDTO.TagMontlyDTO> tagMontlyDTOS = logRepository.findMontlyTagAll(memberId, resultDate);

        ResLogDTO.ResMontlyLogDTO resMontlyLogDTO = new ResLogDTO.ResMontlyLogDTO();
        resMontlyLogDTO.setAiFeedbacks(letterMontlyDTOS);
        resMontlyLogDTO.setEmotions(emotionDTOS);
        resMontlyLogDTO.setTags(tagMontlyDTOS);

        return resMontlyLogDTO;
    }

    /*
     * 주간 도약기록 조회
     * @param : memberId(Long)
     * */
    @Transactional
    public ResLogDTO.ResWeeklyLogDTO getWeeklyLogDetail(Long memberId, String creationDate) {

        // String 문자열 LocalDateTime으로 변환
        Date resultDate = null;
        try {
            resultDate = dateUtil.stringToLocalDateTime(creationDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        log.info("resultDate {}", resultDate);
        ResLogDTO.ResWeeklyLogDTO resWeeklyLogDTO = new ResLogDTO.ResWeeklyLogDTO();
        List<ResLetterDTO.LetterWeeklyDTO> letterWeeklyDTOS = logRepository.findLetterByDate(memberId, resultDate);
        log.info("로그 서비스 여기에 찍히닝?!");
//        List<EmotionDTO> emotionDTOS = logRepository.findEmotionByDate(memberId, resultDate);
//        List<ResTagDTO.TagWeeklyDTO> tagWeeklyDTOS = logRepository.findTagsByName(memberId, resultDate);

        resWeeklyLogDTO.setAiFeedbacks(letterWeeklyDTOS);
//        resWeeklyLogDTO.setEmotions(emotionDTOS);
//        resWeeklyLogDTO.setTags(tagWeeklyDTOS);
        return resWeeklyLogDTO;
    }

    /*
     * 일간 도약기록 조회
     * @param : memberId(Long), logId(Long)
     * */
    public List<ResLogDTO.ResDailyLogDTO> getDailyLogDetail(Long memberId, Long logId) {

        List<ResLogDTO.ResDailyLogDTO> resDailyLogDTOS = logRepository.findLogByLogIdAndMemberId(memberId, logId);

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
    public ResLogDTO.ResLogAddDTO setLogAdd(ReqLogDTO reqLogDTO, Long memberId) {

        // 도약기록 insert 전 회원 존재하는지 isExists 확인
         boolean isExistsMember = memberRepository.existsByMemberId(memberId);

         ResLogDTO.ResLogAddDTO resLogAddDTO = new ResLogDTO.ResLogAddDTO();
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
             for(ResTagDTO resTagDTO : reqLogDTO.getTagNameList()){
                 Tag tag = Tag.builder()
                         .name(resTagDTO.getTagName())
                         .build();
                 entityManager.persist(tag);
                 setLogTag(log, tag);
             }

             // 레벨 update
             Level level = levelRepository.findLevelByMemberId(memberId).orElseThrow();
             level.updateWhenPostLog();
             levelRepository.save(level);

             resLogAddDTO.setLogId(log.getLogId());
             resLogAddDTO.setMemberId(selectByMember.getMemberId());
             resLogAddDTO.setLogContent("도약기록 작성을 완료했습니다.");
         }

         // 회원이 존재하지 않다면
        return resLogAddDTO;
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
