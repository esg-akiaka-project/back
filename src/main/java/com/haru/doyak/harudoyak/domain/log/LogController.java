package com.haru.doyak.harudoyak.domain.log;

import com.haru.doyak.harudoyak.dto.letter.ReqLetterDTO;
import com.haru.doyak.harudoyak.dto.log.ReqLogDTO;
import com.haru.doyak.harudoyak.dto.log.ReqWeeklyLogDTO;
import com.haru.doyak.harudoyak.dto.log.ResLogDTO;
import com.haru.doyak.harudoyak.dto.log.ResDailyLogDTO;
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
     * 도약 기록
     * */

    /*
     * 주간 도약기록 조회
     * @param : memberId(Long), logId(Long)
     * */
    @GetMapping("weekly/{memberId}")
    public void getWeeklyLogDetail(@PathVariable("memberId") Long memberId, @RequestBody ReqWeeklyLogDTO reqWeeklyLogDTO) {
        log.info("주간 도약기록 조회~~~");
    }
    /*
    * 일간 도약기록 조회
    * @param : memberId(Long), logId(Long)
    * */
    @GetMapping("{memberId}/{logId}")
    public ResponseEntity<List<ResDailyLogDTO>> getDailyLogDetail(@PathVariable("memberId") Long memberId, @PathVariable("logId") Long logId) {
        log.info("memberId ------> {}", memberId);
        log.info("logId ------> {}", logId);
        List<ResDailyLogDTO> resDailyLogDTOS = logService.getDailyLogDetail(memberId, logId);
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
    public ResponseEntity<ResLogDTO> setLogAdd(@PathVariable("memberId") Long memberId, @RequestBody ReqLogDTO reqLogDTO) {
        ResLogDTO resLogDTO = logService.setLogAdd(reqLogDTO, memberId);
        return ResponseEntity.ok().body(resLogDTO);
    }

}
