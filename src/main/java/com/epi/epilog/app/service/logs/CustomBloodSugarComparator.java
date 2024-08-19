package com.epi.epilog.app.service.logs;

import com.epi.epilog.app.dto.DiabetesResponseDto;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class CustomBloodSugarComparator implements Comparator<DiabetesResponseDto.DiabetesBloodSugar> {
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
    public int compare(DiabetesResponseDto.DiabetesBloodSugar o1, DiabetesResponseDto.DiabetesBloodSugar o2) {
        int orderComparison = Integer.compare(getOrder(o1.getOccurrenceType()), getOrder(o2.getOccurrenceType()));
        if (orderComparison != 0) {
            return orderComparison;
        }

        // If the occurrence types have the same order, compare by time
        return compareTimes(o1.getOccurrenceType(), o2.getOccurrenceType());
    }

    private int getOrder(String occurrenceType) {
        if (EVENT_ORDER.containsKey(occurrenceType)) {
            return EVENT_ORDER.get(occurrenceType);
        } else if (occurrenceType.length() >= 11) {
            LocalTime parsedTime = LocalTime.parse(occurrenceType.substring(11), TIME_FORMATTER);
            if (isBetween(parsedTime, LocalTime.of(2, 0), LocalTime.of(7, 0))) {
                return 0;
            } else if (isBetween(parsedTime, LocalTime.of(7, 0), LocalTime.of(12, 0))) {
                return 3;
            } else if (isBetween(parsedTime, LocalTime.of(12, 0), LocalTime.of(17, 0))) {
                return 6;
            } else if (isBetween(parsedTime, LocalTime.of(17, 0), LocalTime.of(23, 59))) {
                return 9;
            } else {
                throw new IllegalArgumentException("Invalid time: " + occurrenceType);
            }
        } else {
            throw new IllegalArgumentException("OccurrenceType is too short to parse time: " + occurrenceType);
        }
    }


    private boolean isBetween(LocalTime time, LocalTime start, LocalTime end) {
        if (start.isBefore(end)) {
            return !time.isBefore(start) && time.isBefore(end);
        } else {
            return !time.isBefore(start) || time.isBefore(end);
        }
    }

//    private int compareTimes(String dateTime1, String dateTime2) {
//        LocalTime parsedTime1 = LocalTime.parse(dateTime1.substring(11), TIME_FORMATTER);
//        LocalTime parsedTime2 = LocalTime.parse(dateTime2.substring(11), TIME_FORMATTER);
//        return parsedTime1.compareTo(parsedTime2);
//    }

    private int compareTimes(String dateTime1, String dateTime2) {
        if (dateTime1.equals(dateTime2)) {
            // 두 개의 occurrenceType이 완전히 동일한 경우
            return 1; // 첫 번째 객체를 더 크다고 판단합니다.
        } else if (dateTime1.length() > 11 && dateTime2.length() > 11) {
            // 둘 다 날짜와 시간을 포함하는 형식인 경우
            LocalTime parsedTime1 = LocalTime.parse(dateTime1.substring(11), TIME_FORMATTER);
            LocalTime parsedTime2 = LocalTime.parse(dateTime2.substring(11), TIME_FORMATTER);
            return parsedTime1.compareTo(parsedTime2);
        } else {
            // 다른 모든 경우, dateTime1을 더 크다고 판단
            return 1;
        }
    }
}
