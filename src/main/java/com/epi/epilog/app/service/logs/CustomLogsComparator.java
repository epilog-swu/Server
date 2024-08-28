package com.epi.epilog.app.service.logs;

import com.epi.epilog.app.domain.logs.Log;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Log 정렬
 */
@Slf4j
public class CustomLogsComparator implements Comparator<Log> {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final Map<String, Integer> EVENT_ORDER = new HashMap<>();

    static {
        EVENT_ORDER.put("아침식사 전", 1);
        EVENT_ORDER.put("아침식사 후", 2);
        EVENT_ORDER.put("점심식사 전", 4);
        EVENT_ORDER.put("점심식사 후", 5);
        EVENT_ORDER.put("저녁식사 전", 7);
        EVENT_ORDER.put("저녁식사 후", 8);
        EVENT_ORDER.put("자기 전", 10);
    }

    @Override
    public int compare(Log log1, Log log2) {
        int orderComparison = Integer.compare(getOrder(log1.getOccurrenceType()), getOrder(log2.getOccurrenceType()));
        if (orderComparison != 0) {
            return orderComparison;
        }

        if (isTimeFormat(log1.getOccurrenceType()) && isTimeFormat(log2.getOccurrenceType())) {
            return compareTimes(log1.getOccurrenceType(), log2.getOccurrenceType());
        }
        return 0;
    }

    private int getOrder(String occurrenceType) {
        if (EVENT_ORDER.containsKey(occurrenceType)) {
            return EVENT_ORDER.get(occurrenceType);
        } else if (occurrenceType.length() > 11 && Character.isDigit(occurrenceType.charAt(0))) {
            try {
                LocalTime parsedTime = LocalTime.parse(occurrenceType.substring(11), TIME_FORMATTER);
                return getTimeBasedOrder(parsedTime);
            } catch (DateTimeParseException e) {
                throw new ApiException(ErrorCode.TIME_PARSING_FAIL);
            }
        } else {
            throw new ApiException(ErrorCode.OCCURENCE_PARSING_FAIL);
        }
    }

    private int getTimeBasedOrder(LocalTime parsedTime) {
        if (isBetween(parsedTime, LocalTime.of(0, 0), LocalTime.of(7, 0))) {
            return 0;
        } else if (isBetween(parsedTime, LocalTime.of(7, 0), LocalTime.of(12, 0))) {
            return 3;
        } else if (isBetween(parsedTime, LocalTime.of(12, 0), LocalTime.of(17, 0))) {
            return 6;
        } else {
            return 9;
        }
    }

    private boolean isBetween(LocalTime time, LocalTime start, LocalTime end) {
        return !time.isBefore(start) && time.isBefore(end);
    }

    private boolean isTimeFormat(String occurrenceType) {
        return occurrenceType.matches("\\d{4}-\\d{2}-\\d{2}.*");
    }

    private int compareTimes(String time1, String time2) {
        try {
            LocalTime parsedTime1 = LocalTime.parse(time1.substring(11), TIME_FORMATTER);
            LocalTime parsedTime2 = LocalTime.parse(time2.substring(11), TIME_FORMATTER);
            return parsedTime1.compareTo(parsedTime2);
        } catch (DateTimeParseException e) {
            throw new ApiException(ErrorCode.TIME_PARSING_FAIL);
        }
    }
}