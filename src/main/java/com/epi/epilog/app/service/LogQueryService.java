package com.epi.epilog.app.service;

import com.epi.epilog.app.domain.log.Log;
import com.epi.epilog.app.domain.member.Member;
import com.epi.epilog.app.dto.CustomUserInfoDto;
import com.epi.epilog.app.dto.LogsResponseDto;
import com.epi.epilog.app.repository.LogRepository;
import com.epi.epilog.app.repository.MemberRepository;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
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
}
