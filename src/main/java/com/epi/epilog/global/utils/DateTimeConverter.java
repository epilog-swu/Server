package com.epi.epilog.global.utils;

import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Configuration
public class DateTimeConverter {
    public static LocalDateTime convertToLocalDateTime(String dateTimeString) {
        // 날짜와 시간 형식 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try {
            // 문자열을 LocalDateTime 객체로 변환
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
            return dateTime;
        } catch (DateTimeParseException e) {
            // 파싱 실패 시 예외 처리
            System.out.println("Invalid date time format: " + dateTimeString);
            return null;
        }
    }
}
