package com.epi.epilog.app.domain.log;

public enum OccurrenceType {
    BEFORE_BREAKFAST("아침식사 전", 1),
    AFTER_BREAKFAST("아침식사 후", 2),
    BEFORE_LUNCH("점심식사 전", 4),
    AFTER_LUNCH("점심식사 후", 5),
    BEFORE_DINNER("저녁식사 전", 7),
    AFTER_DINNER("저녁식사 후", 8),
    BEFORE_SLEEP("자기 전", 10);

    private final String value;
    private final Integer order;

    OccurrenceType(String value, Integer order) {
        this.value = value;
        this.order = order;
    }

    public String getValue() {
        return value;
    }
    public Integer getOrder() {return order;}

    public static boolean isValid(String value) {
        for (OccurrenceType type : OccurrenceType.values()) {
            if (type.getValue().equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static int getOrder(String value) {
        for (OccurrenceType type : OccurrenceType.values()) {
            if (type.getValue().equals(value)) {
                return type.getOrder();
            }
        }
        return Integer.MAX_VALUE; // HH:mm 형식의 문자열은 가장 뒤로 정렬
    }
}