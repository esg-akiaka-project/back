package com.haru.doyak.harudoyak.domain.log;

import com.haru.doyak.harudoyak.dto.letter.ReqLetterDTO;
import com.haru.doyak.harudoyak.dto.log.*;
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

    /*
     * 월간 도약기록 조회
     * @param : memberId(Long), creationDate(LocalDateTime)
     * */
    @GetMapping("montly/{memberId}/{creationDate}")
    public ResponseEntity<ResLogDTO.ResMontlyLogDTO> getMontlyLogDetail(@PathVariable("memberId") Long memberId, @PathVariable("creationDate") String creationDate) {
        log.info("월간 도약기록 조회 memberId {}", memberId);
        log.info("월간 도약기록 조회 date야 넘어왔니? {}", creationDate);
        ResLogDTO.ResMontlyLogDTO resMontlyLogDTO = logService.getMontlyLogDetail(memberId, creationDate);
        return ResponseEntity.ok(resMontlyLogDTO);
    }

    /*
     * 주간 도약기록 조회
     * @param : memberId(Long), creationDate(LocalDateTime)
     * */
    @GetMapping("weekly/{memberId}/{creationDate}")
    public ResponseEntity<ResLogDTO.ResWeeklyLogDTO> getWeeklyLogDetail(@PathVariable("memberId") Long memberId, @PathVariable("creationDate") String creationDate) {

        log.info("memberId {}", memberId);
        log.info("date야 넘어왔니? {}", creationDate);
        ResLogDTO.ResWeeklyLogDTO resWeeklyLogDTOS = logService.getWeeklyLogDetail(memberId, creationDate);

        return ResponseEntity.ok(resWeeklyLogDTOS);
    }

    /*
    * 일간 도약기록 조회
    * @param : memberId(Long), logId(Long)
    * */
    @GetMapping("daily/{memberId}/{logId}")
    public ResponseEntity<List<ResLogDTO.ResDailyLogDTO>> getDailyLogDetail(@PathVariable("memberId") Long memberId, @PathVariable("logId") Long logId) {
        log.info("memberId ------> {}", memberId);
        log.info("logId ------> {}", logId);
        List<ResLogDTO.ResDailyLogDTO> resDailyLogDTOS = logService.getDailyLogDetail(memberId, logId);
        return ResponseEntity.ok(resDailyLogDTOS);
    }

    /*
    * 도약이 편지 작성
    * @param : memberId(Long), logId(Long), letterContent(String)
    * @return :
    * */
    @PostMapping("letters/{memberId}/{logId}")
    public ResponseEntity<String> setLetterAdd(@PathVariable("memberId") Long memberId, @PathVariable("logId") Long LogId,@RequestBody ReqLetterDTO reqLetterDTO) {
        logService.setLetterAdd(memberId, LogId, reqLetterDTO);
        return ResponseEntity.ok("작성이 완료되었습니다.");
    }

    /*
     * 도약 기록 목록
     * req : memberId(Long)
     * res : logId(Long), creationDate(Date)
     * */
    @GetMapping("list/{memberId}")
    public ResponseEntity<List<ResLogDTO>> getLogList(@PathVariable("memberId") Long memberId){
        List<ResLogDTO> resLogDTOS = logService.getLogList(memberId);
        return ResponseEntity.ok().body(resLogDTOS);
    }

    /*
     * 도약 기록 작성
     * req : memberId(Long), logImage(MultipartFile),
     *       logContent(String), tagName(String []), emotion(String)
     * res : 200 ok 400 등
     * */
    @PostMapping("{memberId}")
    public ResponseEntity<ResLogDTO.ResLogAddDTO> setLogAdd(@PathVariable("memberId") Long memberId, @RequestBody ReqLogDTO reqLogDTO) {
        ResLogDTO.ResLogAddDTO resLogAddDTO = logService.setLogAdd(reqLogDTO, memberId);
        return ResponseEntity.ok().body(resLogAddDTO);
    }

}
