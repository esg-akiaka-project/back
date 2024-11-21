package com.haru.doyak.harudoyak.domain.log;

import com.haru.doyak.harudoyak.dto.letter.ReqLetterDTO;
import com.haru.doyak.harudoyak.dto.log.ReqLogDTO;
import com.haru.doyak.harudoyak.dto.log.ResLogDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/logs/*")
@Slf4j
public class LogController {
    private final LogService logService;

    /**
     * 월간 도약기록 조회
     * @param memberId 회원pk
     * @param creationDate 도약기록 조회 일자
     * @return resMonthlyLogDTO emotions(월간 가장 많이 사용한 감정 3가지 통계), tags(월간 가장 많이 사용한 태그 10가지 통계),
     *                          aiFeedbacks(주간 AI 피드백 횟수)
     */
    @GetMapping("monthly/{memberId}/{creationDate}")
    public ResponseEntity<ResLogDTO.ResMonthlyLogDTO> getMontlyLogDetail(@PathVariable("memberId") Long memberId, @PathVariable("creationDate") String creationDate) {
        log.info("월간 도약기록 조회 memberId {}", memberId);
        log.info("월간 도약기록 조회 date야 넘어왔니? {}", creationDate);
        ResLogDTO.ResMonthlyLogDTO resMonthlyLogDTO = logService.getMontlyLogDetail(memberId, creationDate);
        return ResponseEntity.ok(resMonthlyLogDTO);
    }

    /**
     * 주간 도약기록 조회
     * @param memberId 회원pk
     * @param creationDate 도약기록 조회일자
     * @return resWeeklyLogDTO emotions(주간 가장 많이 사용한 감정 3가지 통계), tags(주간 가장 많이 사용한 태그 10가지 통계),
     *                         aiFeedbacks(주간 AI 피드백 내용 목록)
     */
    @GetMapping("weekly/{memberId}/{creationDate}")
    public ResponseEntity<ResLogDTO.ResWeeklyLogDTO> getWeeklyLogDetail(@PathVariable("memberId") Long memberId, @PathVariable("creationDate") String creationDate) {
        ResLogDTO.ResWeeklyLogDTO resWeeklyLogDTOS = logService.getWeeklyLogDetail(memberId, creationDate);
        return ResponseEntity.ok(resWeeklyLogDTOS);
    }

    /**
     * 일간 도약기록 상세 조회
     * @param memberId 회원pk
     * @param logId 도약기록pk
     * @return resDailyLogDTOS emotion(오늘의 감정), logContent(도약기록 내용), logImageUrl(도약기록 이미지파일url),
     *                         tagNameList(태그들), letterContent(AI 피드백 내용), letterCreationDate(도약기록 작성일자)
     */
    @GetMapping("daily/{memberId}/{logId}")
    public ResponseEntity<List<ResLogDTO.ResDailyLogDTO>> getDailyLogDetail(@PathVariable("memberId") Long memberId, @PathVariable("logId") Long logId) {
        List<ResLogDTO.ResDailyLogDTO> resDailyLogDTOS = logService.getDailyLogDetail(memberId, logId);
        return ResponseEntity.ok(resDailyLogDTOS);
    }

    /**
     * 도약이 편지 작성
     * @param memberId 회원pk
     * @param LogId 도약기록pk
     * @param reqLetterDTO letterContent(AI 피드백 내용)
     * @return body 작성 완료 메세지
     */
    @PostMapping("letters/{memberId}/{logId}")
    public ResponseEntity<String> setLetterAdd(@PathVariable("memberId") Long memberId, @PathVariable("logId") Long LogId,@RequestBody ReqLetterDTO reqLetterDTO) {
        logService.setLetterAdd(memberId, LogId, reqLetterDTO);
        return ResponseEntity.ok("작성이 완료되었습니다.");
    }

    /**
     * 도약 기록 목록을 조회
     * @param memberId 회원pk
     * @return resLogDTOS logId(도약기록pk), creationDate(도약기록 작성일자)을 반환
     */
    @GetMapping("list/{memberId}")
    public ResponseEntity<List<ResLogDTO>> getLogList(@PathVariable("memberId") Long memberId){
        List<ResLogDTO> resLogDTOS = logService.getLogList(memberId);
        return ResponseEntity.ok().body(resLogDTOS);
    }

    /**
     * 도약 기록 작성
     * @param memberId 회원pk
     * @param reqLogDTO logContent(도약기록 내용), tagNameList(도약기록 태그 목록), emotion(오늘의 감정), logImageUrl(이미지 파일url)
     * @return resLogAddDTO logId(도약기록pk), memberId(회원pk), logContent(작성 성공 메세지)를 반환
     */
    @PostMapping("{memberId}")
    public ResponseEntity<ResLogDTO.ResLogAddDTO> setLogAdd(@PathVariable("memberId") Long memberId, @RequestBody ReqLogDTO reqLogDTO) {
        ResLogDTO.ResLogAddDTO resLogAddDTO = logService.setLogAdd(memberId, reqLogDTO);
        return ResponseEntity.ok().body(resLogAddDTO);
    }

}
