package com.epi.epilog.global.utils;

import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Configuration
@Slf4j
public class DateTimeConverter {
    public static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static DateTimeFormatter krDateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
    public static DateTimeFormatter krTimeFormatter = DateTimeFormatter.ofPattern("H시 m분");
    public static DateTimeFormatter krShortTimeFormatter = DateTimeFormatter.ofPattern("H시");

    /**
     * 날짜 문자열 -> LocalDate 변환
     * @param dateString
     * @return
     */
    public static LocalDate convertToLocalDate(String dateString) {
        // 날짜 형식 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            LocalDate date = LocalDate.parse(dateString, formatter);
            return date;
        } catch (DateTimeParseException e) {
            log.info("Invalid date format: " + dateString);
            throw new ApiException(ErrorCode.INVALID_DATETIME_ERROR);
        }
    }

    /**
     * 날짜+시간 문자열 -> LocalDateTime 변환
     * @param dateTimeString
     * @return
     */
    public static LocalDateTime convertToLocalDateTime(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
            return dateTime;
        } catch (DateTimeParseException e) {
            log.info("Invalid date time format: " + dateTimeString);
            throw new ApiException(ErrorCode.INVALID_DATETIME_ERROR);
        }
    }

    /**
     * LocalDate -> 날짜 문자열
     * (yyyy-mm-dd)
     * @param date
     * @return
     */
    public static String convertLocalDateToString(LocalDate date){
        try {
            return dateFormatter.format(date);
        } catch(DateTimeParseException e){
            log.info("Invalid date format: " + date);
            throw new ApiException(ErrorCode.INVALID_DATE_ERROR);
        }
    }

    /**
     * LocalDateTime -> 날짜+시간 문자열
     * (yyyy-mm-dd hh:mm:ss)
     * @param dateTime
     * @return
     */
    public static String convertLocalDateTimeToString(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try{
            return dateTime.format(formatter);
        } catch(DateTimeParseException e){
            log.info("Invalid date time format: " + dateTime);
            throw new ApiException(ErrorCode.INVALID_DATETIME_ERROR);
        }
    }

    /**
     * 분(mm)을 포함할지, 안 포함할지 결정
     * @param time
     * @return
     */
    public static String formatTime(LocalDateTime time) {
        DateTimeFormatter formatter = time.getMinute() == 0 ?
                DateTimeConverter.krShortTimeFormatter :
                DateTimeConverter.krTimeFormatter;
        return time.format(formatter);
    }
}
