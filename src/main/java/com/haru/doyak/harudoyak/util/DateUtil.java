package com.haru.doyak.harudoyak.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateUtil {

    /*
    * String 타입 문자열을 LocalDateTime 타입으로 변환하는 메서드
    * @param : String dateStr
    * @return : LocalDateTime date
    * */
    public LocalDateTime stringToLocalDateTime(String dateStr) {

        dateStr += " 00:00:00";
        // 포맷터
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 문자열 -> Date
        LocalDateTime date = LocalDateTime.parse(dateStr, formatter);
        System.out.println(date); // 2021-06-19T21:05:07

        return date;
    }


}
