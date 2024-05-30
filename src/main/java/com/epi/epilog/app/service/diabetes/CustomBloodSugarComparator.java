package com.epi.epilog.app.service.diabetes;

import com.epi.epilog.app.dto.DiabetesResponseDto;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class CustomBloodSugarComparator implements Comparator<DiabetesResponseDto.DiabetesBloodSugar> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
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
        return getOrder(o1.getOccurrenceType()) - getOrder(o2.getOccurrenceType());
    }

    private int getOrder(String dateTime) {
        if (EVENT_ORDER.containsKey(dateTime)) {
            return EVENT_ORDER.get(dateTime);
        } else {
            LocalTime parsedTime = LocalTime.parse(dateTime.substring(11), TIME_FORMATTER);
            if (isBetween(parsedTime, LocalTime.of(2, 0), LocalTime.of(7, 0))) {
                return 0;
            } else if (isBetween(parsedTime, LocalTime.of(7, 0), LocalTime.of(12, 0))) {
                return 3;
            } else if (isBetween(parsedTime, LocalTime.of(12, 0), LocalTime.of(17, 0))) {
                return 6;
            } else if (isBetween(parsedTime, LocalTime.of(17, 0), LocalTime.of(2, 0))) {
                return 9;
            } else {
                throw new IllegalArgumentException("Invalid time: " + dateTime);
            }
        }
    }

    private boolean isBetween(LocalTime time, LocalTime start, LocalTime end) {
        if (start.isBefore(end)) {
            return !time.isBefore(start) && time.isBefore(end);
        } else {
            return !time.isBefore(start) || time.isBefore(end);
        }
    }
}
