package com.epi.epilog.app.service.logs;

import com.epi.epilog.app.domain.logs.Log;
import com.epi.epilog.app.domain.member.Member;
import com.epi.epilog.app.domain.logs.OccurrenceType;
import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.dto.CustomUserInfoDto;
import com.epi.epilog.app.dto.DiabetesRequestDto;
import com.epi.epilog.app.repository.LogRepository;
import com.epi.epilog.app.repository.MemberRepository;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DiabetesCommandService {
    private final LogRepository logRepository;
    private final MemberRepository memberRepository;
    private final Pattern TIME_PATTERN = Pattern.compile("^\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01]) (0[0-9]|1[0-9]|2[0-3]):(0[1-9]|[0-5][0-9]):(0[1-9]|[0-5][0-9])$");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");



    public CommonResponseDto.CommonResponse createBloodSugar(
            DiabetesRequestDto.BloodSugarRequest form, CustomUserInfoDto member) {
        Member mem = memberRepository.findById(member.getId()).orElseThrow(()->
                new ApiException(ErrorCode.USER_NOT_FOUND));

        List<Log> diabetsList = logRepository.findAllByDateAndMember(form.getDate(), mem);
        if (diabetsList.size() >= 10) {
            throw new ApiException(ErrorCode.OVER_COUNT_DIABETES);
        }

        // create diabetes
        Log diabet = Log.builder()
                .member(mem)
                .isBloodSugar(true)
                .isFall(false)
                .isBloodPressure(false)
                .isWeight(false)
                .isExercise(false)
                .isMood(false)
                .date(form.getDate()!=null?form.getDate():LocalDate.now())
                .occurrenceType(form.getOccurrenceType())
                .title(createTitle(mem, form.getDate(), form.getOccurrenceType()))
                .bloodSugar(form.getBloodSugar())
                .build();
        logRepository.save(diabet);

        // return form
        return CommonResponseDto.CommonResponse.builder()
                .message("일지가 추가됐습니다. - "+diabet.getTitle())
                .success(true)
                .build();
    }

    /**
     * 특정 날짜의 occurrenceType을 이용해서 title 생성
     * @param member
     * @param date
     * @param occurrenceType
     * @return
     */
    public String createTitle(Member member, LocalDate date, String occurrenceType) {
        if (OccurrenceType.isValid(occurrenceType)) {
            List<Log> logs = logRepository.findAllByDateAndMember(date, member);
            Map<String, Integer> titleCount = logs.stream()
                    .collect(Collectors.toMap(
                            Log::getOccurrenceType,
                            data -> 1,
                            Integer::sum
                    ));

            titleCount.put(occurrenceType, titleCount.getOrDefault(occurrenceType, 0) + 1);
            int count = titleCount.get(occurrenceType);
            if (count == 1) {
                return occurrenceType;
            } else {
                return (occurrenceType + "(" + count + ")");
            }
        } else {
            if (TIME_PATTERN.matcher(occurrenceType).matches()) {
                if (occurrenceType.length() >= 19) { // Check if occurrenceType has the correct length
                    String timePart = occurrenceType.substring(11); // Extract the time part from the string
                    LocalTime time = LocalTime.parse(timePart, TIME_FORMATTER);
                    return String.format("%02d시 %02d분", time.getHour(), time.getMinute());
                } else {
                    throw new ApiException(ErrorCode.INVALID_DATETIME_ERROR);
                }
            } else {
                throw new ApiException(ErrorCode.INVALID_DATETIME_ERROR);
            }
        }
    }
}
