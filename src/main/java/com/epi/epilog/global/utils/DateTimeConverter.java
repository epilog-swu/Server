package com.epi.epilog.global.utils;

import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.swing.text.DateFormatter;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Configuration
@Slf4j
public class DateTimeConverter {

    public static LocalDate convertToLocalDate(String dateString) {
        // 날짜 형식 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            LocalDate date = LocalDate.parse(dateString, formatter);
            return date;
        } catch (DateTimeParseException e) {
            // 파싱 실패 시 예외 처리
            log.info("Invalid date format: " + dateString);
            throw new ApiException(ErrorCode.INVALID_DATETIME_ERROR);
        }
    }

    public static LocalDateTime convertToLocalDateTime(String dateTimeString) {
        // 날짜와 시간 형식 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
            return dateTime;
        } catch (DateTimeParseException e) {
            // 파싱 실패 시 예외 처리
            log.info("Invalid date time format: " + dateTimeString);
            throw new ApiException(ErrorCode.INVALID_DATETIME_ERROR);
        }
    }
}
