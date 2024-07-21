package com.epi.epilog.app.service.logs;

import com.epi.epilog.app.domain.log.Log;
import com.epi.epilog.app.domain.log.OccurrenceType;
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
import java.util.*;
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
        logs.sort(new CustomLogsComparator());

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

    /**
     * 일별 평균, 식전후 평균 혈당 조회
     * @param customUserDetails
     * @param queryDate
     * @return
     */
    public LogsResponseDto.DayAvgBloodSugar dayAvgBloodSugar(CustomUserDetails customUserDetails, LocalDate queryDate) {
        Member member = memberRepository.findById(customUserDetails.getMember().getId())
                .orElseThrow(()->new ApiException(ErrorCode.USER_NOT_FOUND));

        List<Log> logs = logRepository.findAllByDateAndMember(queryDate, member);

        // get average
        Double average = logs.stream()
                .filter(log -> log.getBloodSugar() != null)
                .mapToDouble(Log::getBloodSugar)
                .average()
                .orElse(0.0);

        // get prevAverage
        Double preAverage = logs.stream()
                .filter(log -> log.getBloodSugar() != null)
                .filter(log -> OccurrenceType.isValid(log.getOccurrenceType()) &&
                        (log.getOccurrenceType().equals(OccurrenceType.BEFORE_BREAKFAST.getValue()) ||
                                log.getOccurrenceType().equals(OccurrenceType.BEFORE_LUNCH.getValue()) ||
                                log.getOccurrenceType().equals(OccurrenceType.BEFORE_DINNER.getValue())))
                .mapToDouble(Log::getBloodSugar)
                .average()
                .orElse(0.0);

        // get postAverage
        Double postAverage = logs.stream()
                .filter(log -> log.getBloodSugar() != null)
                .filter(log -> OccurrenceType.isValid(log.getOccurrenceType()) &&
                        (log.getOccurrenceType().equals(OccurrenceType.AFTER_BREAKFAST.getValue()) ||
                                log.getOccurrenceType().equals(OccurrenceType.AFTER_LUNCH.getValue()) ||
                                log.getOccurrenceType().equals(OccurrenceType.AFTER_DINNER.getValue())))
                .mapToDouble(Log::getBloodSugar)
                .average()
                .orElse(0.0);


        return LogsResponseDto.DayAvgBloodSugar
                .builder()
                .date(DateTimeConverter.convertLocalDateToString(queryDate))
                .average(average)
                .preAverage(preAverage)
                .postAverage(postAverage)
                .build();
    }

    /**
     * 일별 혈당 목록 조회
     * @param userInfo
     * @param date
     * @return
     */
    public LogsResponseDto.DayBloodSugarList dayBloodSugarList(CustomUserDetails userInfo, String date) {
        Member member = memberRepository.findById(userInfo.getMember().getId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        LocalDate queryDate = DateTimeConverter.convertToLocalDate(date);
        List<Log> logs = logRepository.findAllByDateAndMember(queryDate, member);
        logs.sort(new CustomLogsComparator());

        List<LogsResponseDto.DayBloodSugarItem> bloodSugarItemList = logs.stream().filter(log -> log.getBloodSugar() != null).map(log ->
                LogsResponseDto.DayBloodSugarItem
                        .builder()
                        .title(log.getTitle())
                        .bloodSugar(log.getBloodSugar())
                        .build()
        ).collect(Collectors.toList());

        return LogsResponseDto.DayBloodSugarList
                .builder()
                .date(date)
                .count(bloodSugarItemList.size())
                .bloodSugars(bloodSugarItemList)
                .build();
    }

    /**
     * 월별 몸무게 및 체지방률 변화 추이 조회
     * @param date
     * @param userInfo
     * @return
     */
    public LogsResponseDto.MonthWeightList getMonthWeightList(String date, CustomUserDetails userInfo) {
        Member member = memberRepository.findById(userInfo.getMember().getId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        LocalDate queryDate = DateTimeConverter.convertToLocalDate(date);
        List<Log> logs = logRepository.findAllByMonthAndMember(queryDate.getYear(), queryDate.getMonthValue(), member);
        logs.sort(new CustomLogsComparator().reversed());

        Map<LocalDate, List<Log>> logsWeightGroupedByDate = logs.stream()
                .filter(log -> log.getWeight() != null)
                .collect(Collectors.groupingBy(Log::getDate, TreeMap::new, Collectors.toList()));

        Map<LocalDate, List<Log>> logsBodyFatPercentGroupedByDate = logs.stream()
                .filter(log -> log.getBodyFatPercentage() != null)
                .collect(Collectors.groupingBy(Log::getDate, TreeMap::new, Collectors.toList()));

        List<LogsResponseDto.MonthWeightItem> weightItemList = new ArrayList<>();
        List<LogsResponseDto.MonthWeightItem> bodyFatPercentage = new ArrayList<>();

        logsWeightGroupedByDate.forEach((logsDate, logList) -> {
            Log lastLog = logList.get(0);
            weightItemList.add(LogsResponseDto.MonthWeightItem.builder()
                    .date(DateTimeConverter.convertLocalDateToString(logsDate))
                    .value(lastLog.getWeight())
                    .build());
        });

        logsBodyFatPercentGroupedByDate.forEach((logsDate, logList) -> {
            Log lastLog = logList.get(0);
            bodyFatPercentage.add(LogsResponseDto.MonthWeightItem.builder()
                            .date(DateTimeConverter.convertLocalDateToString(logsDate))
                            .value(lastLog.getBodyFatPercentage())
                            .build());
        });

        return LogsResponseDto.MonthWeightList.builder()
                .year(queryDate.getYear())
                .month(queryDate.getMonthValue())
                .dayWeight(weightItemList)
                .dayBodyFatPercentage(bodyFatPercentage)
                .build();
    }
}
