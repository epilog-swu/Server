package com.epi.epilog.app.domain.enums;

public enum OccurrenceType {
    BEFORE_BREAKFAST("아침식사 전"),
    AFTER_BREAKFAST("아침식사 후"),
    BEFORE_LUNCH("점심식사 전"),
    AFTER_LUNCH("점심식사 후"),
    BEFORE_DINNER("저녁식사 전"),
    AFTER_DINNER("저녁식사 후"),
    BEFORE_SLEEP("자기 전");
    private final String value;

    OccurrenceType(String description) {
        this.value = description;
    }

    public String getDescription() {
        return this.value;
    }
}
