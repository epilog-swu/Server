package com.epi.epilog.app.domain.enums;

/**
 * Member 연령대
 */
public enum AgeType {
    AGE_15_18("15-18세"),
    AGE_19_29("19-29세"),
    AGE_30_49("30-49세"),
    AGE_50_64("50-64세"),
    AGE_65_74("65-74세"),
    AGE_75_PLUS("75세 이상");

    private final String label;

    AgeType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
