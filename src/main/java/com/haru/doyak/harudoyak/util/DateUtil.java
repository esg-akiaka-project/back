package com.haru.doyak.harudoyak.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class DateUtil {

    /*
    * String 타입 문자열을 LocalDateTime 타입으로 변환하는 메서드
    * @param : String dateStr
    * @return : LocalDateTime date
    * */
    public LocalDateTime stringToLocalDateTime(String dateStr) throws ParseException {

        dateStr += " 00:00:00";

        // 포맷터
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 문자열 -> LocalDateTime 타입으로 변환
        LocalDateTime resultDate = LocalDateTime.parse(dateStr, formatter);

        return resultDate;
    }

//    /*
//     * String 타입 문자열을 LocalDateTime 타입으로 변환하는 메서드
//     * @param : String dateStr
//     * @return : LocalDateTime date
//     * */
//    public Date stringToDate(String dateStr) throws ParseException {
//
//        dateStr += " 00:00:00";
//        // 포맷터
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        // 문자열 -> Date
//        Date date = formatter.parse(dateStr);
//        System.out.println(date); // 2021-06-19T21:05:07
//
//        return date;
//    }


}
