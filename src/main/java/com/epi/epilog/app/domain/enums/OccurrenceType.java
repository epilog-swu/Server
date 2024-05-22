package com.epi.epilog.app.domain.enums;

import com.epi.epilog.app.domain.validators.OccurenceTypeValidator;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.util.regex.Pattern;

@JsonDeserialize(using = OccurrenceType.OccurrenceTypeDeserializer.class)
public enum OccurrenceType {
    BEFORE_BREAKFAST("아침식사 전"),
    AFTER_BREAKFAST("아침식사 후"),
    BEFORE_LUNCH("점심식사 전"),
    AFTER_LUNCH("점심식사 후"),
    BEFORE_DINNER("저녁식사 전"),
    AFTER_DINNER("저녁식사 후"),
    BEFORE_SLEEP("자기 전"),
    TIME_FORMAT("시간 포맷");

    private final String value;

    OccurrenceType(String value) {
        this.value = value;
    }

    public String getDescription() {
        return this.value;
    }

    public static class OccurrenceTypeDeserializer extends JsonDeserializer<OccurrenceType> {

        private static final Pattern TIME_PATTERN = Pattern.compile("^([01]\\d|2[0-3]):([0-5]\\d)$");

        @Override
        public OccurrenceType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String value = p.getText();

            for (OccurrenceType type : OccurrenceType.values()) {
                if (type.getDescription().equals(value)) {
                    return type;
                }
            }

            if (TIME_PATTERN.matcher(value).matches()) {
                // Special handling for time patterns, you can use a special constant or handle it differently
                return OccurrenceType.TIME_FORMAT; // TIME_FORMAT은 ENUM에 추가된 특수한 값으로 가정
            }

            throw new ApiException(ErrorCode.INVALID_FORMAT_ERROR);
        }
    }
}