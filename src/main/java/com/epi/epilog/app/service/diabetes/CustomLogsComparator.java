package com.epi.epilog.app.service.diabetes;

import com.epi.epilog.app.domain.log.Log;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

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
//        log.info("order comparison: " + orderComparison);
        if (orderComparison != 0) {
            return orderComparison;
        }

        // If the occurrence types are the same, compare by time
        return compareTimes(log1.getOccurrenceType(), log2.getOccurrenceType());
    }

    private int getOrder(String occurrenceType) {
        if (EVENT_ORDER.containsKey(occurrenceType)) {
            return EVENT_ORDER.get(occurrenceType);
        } else {
            LocalTime parsedTime = LocalTime.parse(occurrenceType.substring(11), TIME_FORMATTER);
            if (isBetween(parsedTime, LocalTime.of(0, 0), LocalTime.of(7, 0))) {
                return 0; // 0시부터 7시까지
            } else if (isBetween(parsedTime, LocalTime.of(7, 0), LocalTime.of(12, 0))) {
                return 3; // 7시부터 12시까지
            } else if (isBetween(parsedTime, LocalTime.of(12, 0), LocalTime.of(17, 0))) {
                return 6; // 12시부터 17시까지
            } else if (isBetween(parsedTime, LocalTime.of(17, 0), LocalTime.of(23, 59))) {
                return 9; // 17시부터 24시까지
            } else {
                throw new IllegalArgumentException("Invalid time: " + occurrenceType);
            }
        }
    }

    private boolean isBetween(LocalTime time, LocalTime start, LocalTime end) {
        return !time.isBefore(start) && time.isBefore(end);
    }

    private int compareTimes(String time1, String time2) {
        LocalTime parsedTime1 = LocalTime.parse(time1.substring(11), TIME_FORMATTER);
        LocalTime parsedTime2 = LocalTime.parse(time2.substring(11), TIME_FORMATTER);
        return parsedTime1.compareTo(parsedTime2);
    }
}
