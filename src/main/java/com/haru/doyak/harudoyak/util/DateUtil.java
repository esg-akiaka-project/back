package com.haru.doyak.harudoyak.util;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class DateUtil {

    /*
    * String 타입 문자열을 LocalDateTime 타입으로 변환하는 메서드
    * @param : String dateStr
    * @return : LocalDateTime date
    * */
    public Date stringToLocalDateTime(String dateStr) throws ParseException {

        dateStr += " 00:00:00";
        // 포맷터
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 문자열 -> Date
        Date date = formatter.parse(dateStr);
        System.out.println(date); // 2021-06-19T21:05:07

        return date;
    }


}
