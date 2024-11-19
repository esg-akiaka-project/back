package com.haru.doyak.harudoyak.domain.log;

import com.haru.doyak.harudoyak.dto.letter.ReqLetterDTO;
import com.haru.doyak.harudoyak.dto.log.*;
import com.haru.doyak.harudoyak.entity.*;
import com.haru.doyak.harudoyak.repository.*;
import com.haru.doyak.harudoyak.util.DateUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogService {
    private final LogRepository logRepository;
    private final MemberRepository memberRepository;
    private final FileRepository fileRepository;
    private final LevelRepository levelRepository;
    private final TagRepository tagRepository;
    private final LogTagRepository logTagRepository;
    private final LetterRepository letterRepository;
    private final DateUtil dateUtil;

    /*
     * 월간 도약기록 조회
     * @param : memberId(Long), creationDate(LocalDateTime)
     * */
    public ResLogDTO.ResMonthlyLogDTO getMontlyLogDetail(Long memberId, String creationDate) {

        // String 문자열 LocalDateTime으로 변환
        LocalDateTime resultLocalDateTime = null;
        try {
            resultLocalDateTime = dateUtil.stringToLocalDateTime(creationDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        // 해당 월의 시작일과 종료일 계산
        LocalDateTime startMonthDayDate = resultLocalDateTime.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay();
        LocalDateTime endMonthDayDate = resultLocalDateTime.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate().atStartOfDay();
        log.info("startMonthDayDate---------------------> {}", startMonthDayDate);
        log.info("endMonthDayDate---------------------> {}", endMonthDayDate);
        List<ResLetterDTO.LetterMonthlyDTO> letterMonthlyDTOS = logRepository.findMontlyLetterAll(memberId, startMonthDayDate, endMonthDayDate);
        List<EmotionDTO.ResEmotionMonthlyDTO> emotionDTOS = logRepository.findMontlyEmotion(memberId, startMonthDayDate, endMonthDayDate);
        List<ResTagDTO.TagMonthlyDTO> tagMonthlyDTOS = logRepository.findMontlyTagAll(memberId, startMonthDayDate, endMonthDayDate);

        ResLogDTO.ResMonthlyLogDTO resMonthlyLogDTO = new ResLogDTO.ResMonthlyLogDTO();
        resMonthlyLogDTO.setAiFeedbacks(letterMonthlyDTOS);
        resMonthlyLogDTO.setEmotions(emotionDTOS);
        resMonthlyLogDTO.setTags(tagMonthlyDTOS);

        return resMonthlyLogDTO;
    }

    /*
     * 주간 도약기록 조회
     * @param : memberId(Long)
     * */
    @Transactional
    public ResLogDTO.ResWeeklyLogDTO getWeeklyLogDetail(Long memberId, String creationDate) {

        // String 문자열 LocalDateTime으로 변환
        LocalDateTime resultLocalDateTime = null;
        try {
            resultLocalDateTime = dateUtil.stringToLocalDateTime(creationDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        // 월요일 날짜 계산
        LocalDateTime mondayDate = resultLocalDateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        // 일요일 날짜 계산
        LocalDateTime sundayDate = resultLocalDateTime.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        log.info("mondayDate---------------------> {}", mondayDate);
        log.info("sundayDate---------------------> {}", sundayDate);
        ResLogDTO.ResWeeklyLogDTO resWeeklyLogDTO = new ResLogDTO.ResWeeklyLogDTO();
        List<ResLetterDTO.LetterWeeklyDTO> letterWeeklyDTOS = logRepository.findLetterByDate(memberId, mondayDate, sundayDate);
        List<EmotionDTO> emotionDTOS = logRepository.findEmotionByDate(memberId, mondayDate, sundayDate).orElseThrow();
        List<ResTagDTO.TagWeeklyDTO> tagWeeklyDTOS = logRepository.findTagsByName(memberId, mondayDate, sundayDate).orElseThrow();

        resWeeklyLogDTO.setAiFeedbacks(letterWeeklyDTOS);
        resWeeklyLogDTO.setEmotions(emotionDTOS);
        resWeeklyLogDTO.setTags(tagWeeklyDTOS);
        return resWeeklyLogDTO;
    }

    /*
     * 일간 도약기록 조회
     * @param : memberId(Long), logId(Long)
     * */
    public List<ResLogDTO.ResDailyLogDTO> getDailyLogDetail(Long memberId, Long logId) {

        List<ResLogDTO.ResDailyLogDTO> resDailyLogDTOS = logRepository.findLogByLogIdAndMemberId(memberId, logId).orElseThrow();

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
            letterRepository.save(letter);
        }
    }

    /*
     * 도약 기록 목록
     * req : memberId(Long)
     * res : logId(Long), creationDate(Date)
     * */
    public List<ResLogDTO> getLogList(Long memberId){

        List<ResLogDTO> resLogDTOS = logRepository.findLogAllByMemberId(memberId).orElseThrow();

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
         Member selectMember = memberRepository.findMemberByMemberId(memberId).orElseThrow();

         ResLogDTO.ResLogAddDTO resLogAddDTO = new ResLogDTO.ResLogAddDTO();
         // 회원이 존재한다면
         if (selectMember.getMemberId() != null){

             if(reqLogDTO.getLogImageUrl() != null){

                 // 이미지파일url insert
                 File file = File.builder()
                         .filePathName(reqLogDTO.getLogImageUrl())
                         .build();
                 fileRepository.save(file);

                 File selectFile = fileRepository.findByFileId(file.getFileId()).orElseThrow();
                 Member selectByMember = memberRepository.findMemberByMemberId(memberId).orElseThrow();

                 // 도약기록 insert
                 Log log = Log.builder()
                         .member(selectByMember)
                         .file(selectFile)
                         .content(reqLogDTO.getLogContent())
                         .emotion(reqLogDTO.getEmotion())
                         .build();
                 logRepository.save(log);

                 // 태그 insert
                 for(ResTagDTO resTagDTO : reqLogDTO.getTagNameList()){
                     Tag tag = Tag.builder()
                             .name(resTagDTO.getTagName())
                             .build();
                     tagRepository.save(tag);
                     LogTag logTag = LogTag.builder()
                             .logTagId(new LogTagId(log.getLogId(), tag.getTagId()))
                             .log(log)
                             .tag(tag)
                             .build();
                     logTagRepository.save(logTag);
                 }
                 resLogAddDTO.setLogId(log.getLogId());
                 resLogAddDTO.setMemberId(selectByMember.getMemberId());
                 resLogAddDTO.setLogContent("도약기록 작성을 완료했습니다.");

             }else {

                 Member selectByMember = memberRepository.findMemberByMemberId(memberId).orElseThrow();
                 // 도약기록 insert
                 Log log = Log.builder()
                         .member(selectByMember)
                         .content(reqLogDTO.getLogContent())
                         .emotion(reqLogDTO.getEmotion())
                         .build();
                 logRepository.save(log);

                 // 태그 insert
                 for(ResTagDTO resTagDTO : reqLogDTO.getTagNameList()){
                     Tag tag = Tag.builder()
                             .name(resTagDTO.getTagName())
                             .build();
                     tagRepository.save(tag);
                     LogTag logTag = LogTag.builder()
                             .logTagId(new LogTagId(log.getLogId(), tag.getTagId()))
                             .log(log)
                             .tag(tag)
                             .build();
                     logTagRepository.save(logTag);
                 }
                 resLogAddDTO.setLogId(log.getLogId());
                 resLogAddDTO.setMemberId(selectByMember.getMemberId());
                 resLogAddDTO.setLogContent("도약기록 작성을 완료했습니다.");

             }

             // 레벨 update
             Level level = levelRepository.findLevelByMemberId(memberId).orElseThrow();
             level.updateWhenPostLog();
             levelRepository.save(level);

             return resLogAddDTO;
         }

         // 회원이 존재하지 않다면
        return resLogAddDTO;
    }

}
