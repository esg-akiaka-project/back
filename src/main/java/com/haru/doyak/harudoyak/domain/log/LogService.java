package com.haru.doyak.harudoyak.domain.log;

import com.haru.doyak.harudoyak.dto.letter.ReqLetterDTO;
import com.haru.doyak.harudoyak.dto.log.*;
import com.haru.doyak.harudoyak.entity.*;
import com.haru.doyak.harudoyak.exception.CustomException;
import com.haru.doyak.harudoyak.exception.ErrorCode;
import com.haru.doyak.harudoyak.repository.*;
import com.haru.doyak.harudoyak.util.DateUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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

    /**
     * 월간 도약기록 조회
     * @param memberId 회원pk
     * @param creationDate 도약기록 조회일자
     * @return resMonthlyLogDTO emotions(월간 가장 많이 사용한 감정 3가지 통계), tags(월간 가장 많이 사용한 태그 10가지 통계),
     *                          aiFeedbacks(주간 AI 피드백 횟수)
     * @throws ParseException String -> Date 타입 변환이 실패 했을 때
     * @throws IllegalArgumentException 객체 필드와 select하는 컬럼명이 매핑이 안됐을 때
     * @throws NullPointerException select하는 값이 비어있을 때
     * @throws Exception 예기치 못한 시스템 에러
     */
    public ResLogDTO.ResMonthlyLogDTO getMontlyLogDetail(Long memberId, String creationDate) {

        try {

            // String 문자열 LocalDateTime으로 변환
            LocalDateTime resultLocalDateTime = dateUtil.stringToLocalDateTime(creationDate);
            // 해당 월의 시작일과 종료일 계산
            LocalDateTime startMonthDayDate = resultLocalDateTime.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay();
            LocalDateTime endMonthDayDate = resultLocalDateTime.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate().atStartOfDay();

            List<ResLetterDTO.LetterMonthlyDTO> letterMonthlyDTOS = logRepository.findMontlyLetterAll(memberId, startMonthDayDate, endMonthDayDate)
                                                                    .orElseThrow(() -> new CustomException(ErrorCode.LETTER_LIST_NOT_FOUND));
            List<EmotionDTO.ResEmotionMonthlyDTO> emotionDTOS = logRepository.findMontlyEmotion(memberId, startMonthDayDate, endMonthDayDate)
                                                                .orElseThrow(() -> new CustomException(ErrorCode.EMOTION_LIST_NOT_FOUND));
            List<ResTagDTO.TagMonthlyDTO> tagMonthlyDTOS = logRepository.findMontlyTagAll(memberId, startMonthDayDate, endMonthDayDate)
                                                           .orElseThrow(() -> new CustomException(ErrorCode.TAG_LIST_NOT_FOUND));

            ResLogDTO.ResMonthlyLogDTO resMonthlyLogDTO = new ResLogDTO.ResMonthlyLogDTO();
            resMonthlyLogDTO.setAiFeedbacks(letterMonthlyDTOS);
            resMonthlyLogDTO.setEmotions(emotionDTOS);
            resMonthlyLogDTO.setTags(tagMonthlyDTOS);

            return resMonthlyLogDTO;

        } catch (ParseException parseException) {
            throw new CustomException(ErrorCode.CONVERSION_FAIL);
        } catch (IllegalArgumentException illegalArgumentException){
            throw new CustomException(ErrorCode.SYNTAX_INVALID_FIELD);
        } catch (NullPointerException nullPointerException){
            throw new CustomException(ErrorCode.EMPTY_VALUE);
        } catch (Exception exception){
            throw new CustomException(ErrorCode.SYSTEM_CONNECTION_ERROR);
        }
    }

    /**
     * 주간 도약기록 조회
     * @param memberId 회원pk
     * @param creationDate 도약기록 조회일자
     * @return resWeeklyLogDTO emotions(주간 가장 많이 사용한 감정 3가지 통계), tags(주간 가장 많이 사용한 태그 10가지 통계),
     *                         aiFeedbacks(주간 AI 피드백 내용 목록)
     * @throws ParseException String -> Date 타입 변환이 실패 했을 때
     * @throws IllegalArgumentException 객체 필드와 select하는 컬럼명이 매핑이 안됐을 때
     * @throws NullPointerException select하는 값이 비어있을 때
     * @throws Exception 예기치 못한 시스템 에러
     */
    @Transactional
    public ResLogDTO.ResWeeklyLogDTO getWeeklyLogDetail(Long memberId, String creationDate) {

        try {

            // String 문자열 LocalDateTime으로 변환
            LocalDateTime resultLocalDateTime = dateUtil.stringToLocalDateTime(creationDate);
            // 월요일 날짜 계산
            LocalDateTime mondayDate = resultLocalDateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            // 일요일 날짜 계산
            LocalDateTime sundayDate = resultLocalDateTime.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

            ResLogDTO.ResWeeklyLogDTO resWeeklyLogDTO = new ResLogDTO.ResWeeklyLogDTO();
            List<ResLetterDTO.LetterWeeklyDTO> letterWeeklyDTOS = logRepository.findLetterByDate(memberId, mondayDate, sundayDate)
                                                                  .orElseThrow(() -> new CustomException(ErrorCode.LETTER_LIST_NOT_FOUND));
            List<EmotionDTO> emotionDTOS = logRepository.findEmotionByDate(memberId, mondayDate, sundayDate)
                                           .orElseThrow(() -> new CustomException(ErrorCode.EMOTION_LIST_NOT_FOUND));
            List<ResTagDTO.TagWeeklyDTO> tagWeeklyDTOS = logRepository.findTagsByName(memberId, mondayDate, sundayDate)
                                                         .orElseThrow(() -> new CustomException(ErrorCode.TAG_LIST_NOT_FOUND));

            resWeeklyLogDTO.setAiFeedbacks(letterWeeklyDTOS);
            resWeeklyLogDTO.setEmotions(emotionDTOS);
            resWeeklyLogDTO.setTags(tagWeeklyDTOS);
            return resWeeklyLogDTO;

        } catch (ParseException parseException) {
            throw new CustomException(ErrorCode.CONVERSION_FAIL);
        } catch (IllegalArgumentException illegalArgumentException){
            throw new CustomException(ErrorCode.SYNTAX_INVALID_FIELD);
        } catch (NullPointerException nullPointerException){
            throw new CustomException(ErrorCode.EMPTY_VALUE);
        } catch (Exception exception){
            throw new CustomException(ErrorCode.SYSTEM_CONNECTION_ERROR);
        }

    }

    /**
     * 일간 도약기록 상세 조회
     * @param memberId 회원pk
     * @param logId 도약기록pk
     * @return resDailyLogDTOS emotion(오늘의 감정), logContent(도약기록 내용), logImageUrl(도약기록 이미지파일url),
     *                         tagNameList(태그들), letterContent(AI 피드백 내용), letterCreationDate(도약기록 작성일자)
     * @throws IllegalArgumentException 객체 필드와 select하는 컬럼명이 매핑이 안됐을 때
     * @throws NullPointerException select하는 값이 비어있을 때
     * @throws Exception 예기치 못한 시스템 에러
     */
    public List<ResLogDTO.ResDailyLogDTO> getDailyLogDetail(Long memberId, Long logId) {
        try {

            List<ResLogDTO.ResDailyLogDTO> resDailyLogDTOS = logRepository.findLogByLogIdAndMemberId(memberId, logId)
                                                             .orElseThrow(() -> new CustomException(ErrorCode.LOG_LIST_NOT_FOUND));
            return resDailyLogDTOS;

        } catch (IllegalArgumentException illegalArgumentException){
            throw new CustomException(ErrorCode.SYNTAX_INVALID_FIELD);
        } catch (NullPointerException nullPointerException){
            throw new CustomException(ErrorCode.EMPTY_VALUE);
        } catch (Exception exception){
            throw new CustomException(ErrorCode.SYSTEM_CONNECTION_ERROR);
        }
    }

    /**
     * 도약이 편지 작성
     * @param memberId 회원pk
     * @param LogId 도약기록pk
     * @param reqLetterDTO letterContent(AI 피드백 내용)
     * @throws DataIntegrityViolationException 잘못 입력된 데이터가 왔을때
     * @throws Exception 예기치 못한 시스템 에러
     */
    @Transactional
    public void setLetterAdd(Long memberId, Long LogId, ReqLetterDTO reqLetterDTO) {

        try {

            // 회원 존재 여부
            Member selectMember = memberRepository.findMemberByMemberId(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

            // 도약 기록 존재 여부
            Log selectLog = logRepository.findLogByLogId(LogId).orElseThrow();

            // 회원과 도약기록이 존재한다면
            if(selectMember.getMemberId() != null && selectLog.getLogId() != null) {

                Letter letter = Letter.builder()
                        .log(selectLog)
                        .content(reqLetterDTO.getLetterContent())
                        .build();
                letterRepository.save(letter);

            }else {
                throw new CustomException(ErrorCode.NULL_VALUE);
            }

        } catch (DataIntegrityViolationException dataIntegrityViolationException){
            throw new CustomException(ErrorCode.LETTER_INVALID_INPUT);
        } catch (Exception exception){
            throw new CustomException(ErrorCode.SYSTEM_CONNECTION_ERROR);
        }

    }

    /**
     * 도약 기록 목록을 조회
     * @param memberId 회원pk
     * @return resLogDTOS logId(도약기록pk), creationDate(도약기록 작성일자)을 반환
     * @throws IllegalArgumentException 객체 필드와 select하는 컬럼명이 매핑이 안됐을 때
     * @throws NullPointerException select하는 값이 비어있을 때 
     * @throws Exception 예기치 못한 시스템 에러
     */
    public List<ResLogDTO> getLogList(Long memberId){
        try {

            List<ResLogDTO> resLogDTOS = logRepository.findLogAllByMemberId(memberId).orElseThrow(() -> new CustomException(ErrorCode.LOG_LIST_NOT_FOUND));
            return resLogDTOS;

        } catch (IllegalArgumentException illegalArgumentException){
            throw new CustomException(ErrorCode.SYNTAX_INVALID_FIELD);
        } catch (NullPointerException nullPointerException){
            throw new CustomException(ErrorCode.EMPTY_VALUE);
        } catch (Exception exception){
            throw new CustomException(ErrorCode.SYSTEM_CONNECTION_ERROR);
        }
    }

    /**
     * 도약 기록 작성
     * @param memberId 회원pk
     * @param reqLogDTO logContent(도약기록 내용), tagNameList(도약기록 태그 목록), emotion(오늘의 감정), logImageUrl(이미지 파일url)
     * @return resLogAddDTO logId(도약기록pk), memberId(회원pk), logContent(작성 성공 메세지)를 반환
     * @throws DataIntegrityViolationException 잘못 입력된 데이터가 왔을때
     * @throws Exception 예기치 못한 시스템 에러
     */
    @Transactional
    public ResLogDTO.ResLogAddDTO setLogAdd(Long memberId, ReqLogDTO reqLogDTO) {
        try {

            // 도약기록 insert 해당 회원이 있는지 확인
            Member selectMember = memberRepository.findMemberByMemberId(memberId).orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

            ResLogDTO.ResLogAddDTO resLogAddDTO = new ResLogDTO.ResLogAddDTO();
            // 회원이 존재한다면
            if (selectMember.getMemberId() != null){

                // 이미지 url이 있다면
                if(reqLogDTO.getLogImageUrl() != null){

                    // 이미지파일url insert
                    File file = File.builder()
                            .filePathName(reqLogDTO.getLogImageUrl())
                            .build();
                    fileRepository.save(file);

                    File selectFile = fileRepository.findByFileId(file.getFileId()).orElseThrow(() ->  new CustomException(ErrorCode.FILE_NOT_FOUND));

                    // 도약기록 insert
                    Log log = Log.builder()
                            .member(selectMember)
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
                    resLogAddDTO.setMemberId(selectMember.getMemberId());
                    resLogAddDTO.setLogContent("도약기록 작성을 완료했습니다.");

                }else {
                    // 이미지 url이 없다면
                    // 도약기록 insert
                    Log log = Log.builder()
                            .member(selectMember)
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
                    resLogAddDTO.setMemberId(selectMember.getMemberId());
                    resLogAddDTO.setLogContent("도약기록 작성을 완료했습니다.");

                }

                // 레벨 update
                Level level = levelRepository.findLevelByMemberId(memberId).orElseThrow(() ->  new CustomException(ErrorCode.LEVEL_NOT_FOUND));
                level.updateWhenPostLog();
                levelRepository.save(level);

                return resLogAddDTO;

            }else {
                throw new CustomException(ErrorCode.NULL_VALUE);
            }

        } catch (DataIntegrityViolationException dataIntegrityViolationException){
            throw new CustomException(ErrorCode.LOG_INVALID_INPUT);
        } catch (Exception exception){
            throw new CustomException(ErrorCode.SYSTEM_CONNECTION_ERROR);
        }
    }

}
