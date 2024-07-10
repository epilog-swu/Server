package com.epi.epilog.app.domain.member;

public enum ActivityLevel {
    LOW("활동 강도 낮음"),
    MEDIUM("일반적인 활동 강도"),
    HIGH("활동 강도 높음");

    private final String label;

    ActivityLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

