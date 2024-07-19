package com.epi.epilog.app.service;

import com.epi.epilog.app.domain.log.Log;
import com.epi.epilog.app.domain.log.LogMood;
import com.epi.epilog.app.domain.member.Member;
import com.epi.epilog.app.dto.CustomUserInfoDto;
import com.epi.epilog.app.dto.LogsResponseDto;
import com.epi.epilog.app.repository.LogExerciseRepository;
import com.epi.epilog.app.repository.LogMoodRepository;
import com.epi.epilog.app.repository.LogRepository;
import com.epi.epilog.app.repository.MemberRepository;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import com.epi.epilog.global.utils.CustomUserDetails;
import com.epi.epilog.global.utils.DateTimeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LogQueryService {
    private final MemberRepository memberRepository;
    private final LogRepository logRepository;
    private final LogMoodRepository logMoodRepository;
    private final LogExerciseRepository logExerciseRepository;

    /**
     * 월별 일지 개수 조회
     * @param date
     * @param member
     * @return
     */
    public LogsResponseDto.MonthLogsCount monthLogsCount(LocalDate date, CustomUserInfoDto member) {
        Member saveMember = memberRepository.findById(member.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Integer year = date.getYear();
        Integer month = date.getMonthValue();

        List<Log> logs = logRepository.findAllByMonthAndMember(year, month, saveMember);

        List<LogsResponseDto.DayLogsCount> counts = countYearMonth(logs, year, month);

        return LogsResponseDto.MonthLogsCount.builder()
                .year(year)
                .month(month)
                .day(counts)
                .build();
    }

    private List<LogsResponseDto.DayLogsCount> countYearMonth(List<Log> logs, Integer year, Integer month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        Integer daysInMonth = yearMonth.lengthOfMonth();

        Map<Integer, Integer> counts = new HashMap<>();

        for (Integer day = 1; day <= daysInMonth; day++){
            counts.put(day, 0);
        }

        for(Log log : logs){
            Integer day = log.getDate().getDayOfMonth();
            counts.put(day, counts.get(day) + 1);
        }

        return counts.entrySet().stream()
                .map(entry -> LogsResponseDto.DayLogsCount.builder()
                        .date(LocalDate.of(year, month, entry.getKey()).toString())
                        .count(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 일별 일지 목록 조회
     * @param member
     * @param queryDate
     * @return
     */
    public LogsResponseDto.DayLogsList dayLogList(CustomUserDetails member, LocalDate queryDate) {
        Member saveMember = memberRepository.findById(member.getMember().getId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        List<LogsResponseDto.DayLogsItem> logs = dayLogItems(saveMember, queryDate);

        return LogsResponseDto.DayLogsList.builder()
                .date(DateTimeConverter.convertLocalDateToString(queryDate))
                .count(logs.size())
                .logs(logs)
                .build();
    }

    private List<LogsResponseDto.DayLogsItem> dayLogItems(Member saveMember, LocalDate queryDate) {
        List<Log> logs = logRepository.findAllByDateAndMember(queryDate, saveMember);
//        log.info("repo logs="+logs);
        return logs.stream().map(log ->
                LogsResponseDto.DayLogsItem
                        .builder()
                        .id(log.getId())
                        .title(log.getTitle())
                        .keyword(getKeywords(log))
                        .build()
        ).collect(Collectors.toList());
    }

    private List<String> getKeywords(Log log) {
        if (log == null)
            return null;
        List<String> keywordList = new ArrayList<>();
        if (log.getBloodSugar() != null)
            keywordList.add("혈당");
        if (log.getWeight() != null || log.getBodyFatPercentage() != null || log.getBodyPhoto() != null)
            keywordList.add("몸무게");
        if (log.getHeartRate() != null || log.getDiastolicBloodPressure() != null || log.getSystolicBloodPressure() != null)
            keywordList.add("혈압");
        if (!logMoodRepository.findByLog(log).isEmpty())
            keywordList.add("기분");
        if (!logExerciseRepository.findByLog(log).isEmpty())
            keywordList.add("운동");
        return keywordList;
    }
}
