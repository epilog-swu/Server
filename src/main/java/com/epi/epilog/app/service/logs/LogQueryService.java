    package com.epi.epilog.app.service.logs;

    import com.epi.epilog.app.domain.logs.Log;
    import com.epi.epilog.app.domain.logs.LogExercise;
    import com.epi.epilog.app.domain.enums.OccurrenceType;
    import com.epi.epilog.app.domain.member.Member;
    import com.epi.epilog.app.dto.CustomUserInfoDto;
    import com.epi.epilog.app.dto.LogsResponseDto;
    import com.epi.epilog.app.dto.PdfData;
    import com.epi.epilog.app.repository.LogExerciseRepository;
    import com.epi.epilog.app.repository.LogRepository;
    import com.epi.epilog.app.repository.MemberRepository;
    import com.epi.epilog.global.exception.ApiException;
    import com.epi.epilog.global.exception.ErrorCode;
    import com.epi.epilog.global.utils.CustomUserDetails;
    import com.epi.epilog.global.utils.DateTimeConverter;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.jetbrains.annotations.NotNull;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.time.LocalDate;
    import java.time.YearMonth;
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;
    import java.util.TreeMap;
    import java.util.Optional;
    import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    @Transactional(readOnly = true)
    public class LogQueryService {
        private final MemberRepository memberRepository;
        private final LogRepository logRepository;
        private final LogExerciseRepository logExerciseRepository;

        /**
         * 월별 일지 개수 조회
         *
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

            for (Integer day = 1; day <= daysInMonth; day++) {
                counts.put(day, 0);
            }

            for (Log log : logs) {
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
         *
         * @param member
         * @param queryDate
         * @return
         */
        public LogsResponseDto.DayLogsList dayLogList(CustomUserDetails member, LocalDate queryDate) {
            Member saveMember = getMember(member);

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
                            .title(log.getDate().format(DateTimeConverter.krDateFormatter) + " " + log.getTitle())
                            .keyword(getKeywords(log))
                            .build()
            ).collect(Collectors.toList());
        }

        private List<String> getKeywords(Log log) {
            if (log == null)
                return null;
            List<String> keywordList = new ArrayList<>();
            if (log.getIsFall())
                keywordList.add("낙상");
            if (log.getBloodSugar() != null)
                keywordList.add("혈당");
            if (log.getHeartRate() != null || log.getDiastolicBloodPressure() != null || log.getSystolicBloodPressure() != null)
                keywordList.add("혈압");
            if (log.getWeight() != null || log.getBodyFatPercentage() != null || log.getBodyPhoto() != null)
                keywordList.add("몸무게");
            if (log.getIsMood() && !log.getLogMood().isEmpty())
                keywordList.add("기분");
            if (log.getIsExercise() && !logExerciseRepository.findByLog(log).isEmpty())
                keywordList.add("운동");
            return keywordList;
        }

        /**
         * 일별 평균, 식전후 평균 혈당 조회
         *
         * @param customUserDetails
         * @param queryDate
         * @return
         */
        public LogsResponseDto.DayAvgBloodSugar dayAvgBloodSugar(CustomUserDetails customUserDetails,
                                                                 LocalDate queryDate) {
            Member member = getMember(customUserDetails);

            List<Log> logs = logRepository.findAllByDateAndMember(queryDate, member);

            Double average = getAverage(logs);
            Double preAverage = getPrePostAverage(logs,
                    OccurrenceType.BEFORE_BREAKFAST,
                    OccurrenceType.BEFORE_LUNCH,
                    OccurrenceType.BEFORE_DINNER);
            Double postAverage = getPrePostAverage(logs,
                    OccurrenceType.AFTER_BREAKFAST,
                    OccurrenceType.AFTER_LUNCH,
                    OccurrenceType.AFTER_DINNER);


            return LogsResponseDto.DayAvgBloodSugar
                    .builder()
                    .date(DateTimeConverter.convertLocalDateToString(queryDate))
                    .average(average)
                    .preAverage(preAverage)
                    .postAverage(postAverage)
                    .build();
        }

        @NotNull
        private static Double getPrePostAverage(List<Log> logs,
                                                OccurrenceType beforeBreakfast,
                                                OccurrenceType beforeLunch,
                                                OccurrenceType beforeDinner) {
            Double preAverage = logs.stream()
                    .filter(log -> log.getBloodSugar() != null)
                    .filter(log -> OccurrenceType.isValid(log.getOccurrenceType()) &&
                            (log.getOccurrenceType().equals(beforeBreakfast.getValue()) ||
                                    log.getOccurrenceType().equals(beforeLunch.getValue()) ||
                                    log.getOccurrenceType().equals(beforeDinner.getValue())))
                    .mapToDouble(Log::getBloodSugar)
                    .average()
                    .orElse(0.0);
            return preAverage;
        }

        @NotNull
        private static Double getAverage(List<Log> logs) {
            Double average = logs.stream()
                    .filter(log -> log.getBloodSugar() != null)
                    .mapToDouble(Log::getBloodSugar)
                    .average()
                    .orElse(0.0);
            return average;
        }

        /**
         * 일별 혈당 목록 조회
         *
         * @param userInfo
         * @param date
         * @return
         */
        public LogsResponseDto.DayBloodSugarList dayBloodSugarList(CustomUserDetails userInfo, String date) {
            Member member = getMember(userInfo);

            LocalDate queryDate = DateTimeConverter.convertToLocalDate(date);
            List<Log> logs = logRepository.findAllByDateAndMember(queryDate, member);
            logs.sort(new CustomLogsComparator());

            List<LogsResponseDto.DayBloodSugarItem> bloodSugarItemList = logs.stream()
                    .filter(log -> log.getBloodSugar() != null).map(log ->
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
         *
         * @param date
         * @param userInfo
         * @return
         */
        public LogsResponseDto.MonthWeightList getMonthWeightList(String date, CustomUserDetails userInfo) {
            Member member = getMember(userInfo);

            LocalDate queryDate = DateTimeConverter.convertToLocalDate(date);
            List<Log> logs = logRepository
                    .findAllByMonthAndMember(queryDate.getYear(), queryDate.getMonthValue(), member);
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

        /**
         * PDF 변환
         *
         * @param userInfo 유저 정보
         * @param start    pdf 변환 시작날짜
         * @param end      pdf 변환 마지막날짜
         * @return
         */
        public List<PdfData> generatedPdfEntries(CustomUserDetails userInfo, LocalDate start, LocalDate end) {
            Member member = getMember(userInfo);

            List<Log> logs = logRepository.findAllByMemberAndStartAndEnd(member, start, end);

            Map<LocalDate, List<Log>> logsByDate = logs.stream()
                    .collect(Collectors.groupingBy(Log::getDate, TreeMap::new, Collectors.toList()));

            List<PdfData> logDetails = new ArrayList<>();
            for (Map.Entry<LocalDate, List<Log>> entry : logsByDate.entrySet()) {
                String date = entry.getKey().toString();
                List<PdfData.PdfLogDetail> details = entry.getValue().stream()
                        .map(logDetail -> {

                            LogsResponseDto.LogDetail exerciseList = createExerciseList(logDetail);
                            LogsResponseDto.LogDetail moodList = createMoodList(logDetail);

                            List<String> icons = getKeywordIcons(logDetail);

                            return PdfData.PdfLogDetail.builder()
                                    .time(Optional.ofNullable(logDetail.getTitle()).orElse(""))
                                    .location(Optional.ofNullable(logDetail.getFallAddress()).orElse(""))
                                    .mapImage(Optional.ofNullable(logDetail.getFallAddressImage()).orElse(""))
                                    .bloodSugar(Optional.ofNullable(logDetail.getBloodSugar()).orElse(0.0))
                                    .systolic(Optional.ofNullable(logDetail.getSystolicBloodPressure()).orElse(0.0))
                                    .diastolic(Optional.ofNullable(logDetail.getDiastolicBloodPressure()).orElse(0.0))
                                    .heartRate(Optional.ofNullable(logDetail.getHeartRate()).orElse(0.0))
                                    .weight(Optional.ofNullable(logDetail.getWeight()).orElse(0.0))
                                    .bodyFat(Optional.ofNullable(logDetail.getBodyFatPercentage()).orElse(0.0))
                                    .bodyImage(Optional.ofNullable(logDetail.getBodyPhoto()).orElse(""))
                                    .physicalActivity(exerciseList)
                                    .mood(moodList)
                                    .icons(icons)
                                    .build();
                        }).collect(Collectors.toList());

                logDetails.add(PdfData.builder().date(date).logs(details).entryCount(details.size()).build());
            }
            return logDetails;
        }

        @NotNull
        private static List<String> getKeywordIcons(Log logDetail) {
            List<String> icons = new ArrayList<>();
            if (logDetail.getIsFall())
                icons.add("icon1");
            if (logDetail.getIsBloodSugar())
                icons.add("icon2");
            if (logDetail.getIsBloodPressure())
                icons.add("icon3");
            if (logDetail.getIsWeight())
                icons.add("icon4");
            if (logDetail.getIsExercise())
                icons.add("icon5");
            if (logDetail.getIsMood())
                icons.add("icon6");
            return icons;
        }

        /**
         * 일지 상세 조회
         *
         * @param principal 유저 정보
         * @param id        Log 아이디
         * @return
         */
        public LogsResponseDto.DetailAllLog getDetailLog(CustomUserDetails principal, Long id) {
            Member member = getMember(principal);

            Log log = logRepository.findById(id)
                    .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

            if (member != log.getMember()) {
                throw new ApiException(ErrorCode.UNAUTHORIZED);
            }

            LogsResponseDto.FallDetail fall = LogsResponseDto.FallDetail.builder()
                    .address(log.getFallAddress() != null ? log.getFallAddress() : null)
                    .mapImage(log.getFallAddressImage() != null ? log.getFallAddressImage() : null)
                    .build();

            List<String> keywords = getKeywords(log);

            // TODO: Optioanl 객체 사용해서 리팩토링 하기. 가독성
            return LogsResponseDto.DetailAllLog.builder()
                    .title(log.getDate() + " " + log.getTitle())
                    .keyword(keywords)
                    .bloodSugar(log.getBloodSugar() != null ? log.getBloodSugar() : null)
                    .systolicBloodPressure(log.getSystolicBloodPressure() != null ? log.getSystolicBloodPressure() : null)
                    .diastolicBloodPressure(log.getDiastolicBloodPressure() != null ? log.getDiastolicBloodPressure() : null)
                    .heartRate(log.getHeartRate() != null ? log.getHeartRate() : null)
                    .weight(log.getWeight() != null ? log.getWeight() : null)
                    .bodyFatPercentage(log.getBodyFatPercentage() != null ? log.getBodyFatPercentage() : null)
                    .bodyPhoto(log.getBodyPhoto() != null ? log.getBodyPhoto() : null)
                    .fall(fall.getAddress() != null || fall.getMapImage() != null ? fall : null)
                    .exercise(!keywords.stream().filter(keyword -> keyword.equals("운동")).collect(Collectors.toList()).isEmpty() ? createExerciseList(log) : null)
                    .mood((!keywords.stream().filter(keyword -> keyword.equals("기분")).collect(Collectors.toList()).isEmpty()) ? createMoodList(log) : null)
                    .build();
        }

        private LogsResponseDto.LogDetail createMoodList(Log log_one) {
            List<String> moodKeyword = new ArrayList<>();
            StringBuilder details = new StringBuilder();

            if (log_one.getLogMood().isEmpty() || log_one.getLogMood() == null) {
                return null;
            }

            log_one.getLogMood().stream()
                    .filter(logMood -> !logMood.getType().equals("직접입력"))
                    .forEach(logMood -> moodKeyword.add(logMood.getType()));

            log_one.getLogMood().stream()
                    .filter(mood -> mood.getType().equals("직접입력"))
                    .filter(mood -> mood.getDetails() != null)
                    .forEach(mood -> details.append(mood.getDetails()));

            String moodComment = createComment(moodKeyword, "mood");

            return LogsResponseDto.LogDetail.builder()
                    .comment(moodComment)
                    .details(details.toString())
                    .keyword(moodKeyword)
                    .build();
        }

        private LogsResponseDto.LogDetail createExerciseList(Log log_one) {
            List<LogExercise> exerciseList;

            exerciseList = logExerciseRepository.findByLog(log_one);

            if (exerciseList.isEmpty() || exerciseList == null) {
                return null;
            }

            List<String> exerciseKeyword = new ArrayList<>();
            StringBuilder details = new StringBuilder();

            exerciseList.stream()
                    .filter(exercise -> !exercise.getType().equals("직접입력"))
                    .forEach(exercise -> exerciseKeyword.add(exercise.getType()));

            exerciseList.stream()
                    .filter(exercise -> exercise.getType().equals("직접입력"))
                    .filter(exercise -> exercise.getDetails() != null)
                    .forEach(exercise -> details.append(exercise.getDetails()));

            String exComment = createComment(exerciseKeyword, "exercise");

            return LogsResponseDto.LogDetail.builder()
                    .comment(exComment)
                    .details(details.toString())
                    .keyword(exerciseKeyword)
                    .build();
        }

        private String createComment(List<String> keyword, String type) {
            StringBuilder comment = new StringBuilder();
            if (type.equals("mood")) {
                comment.append("오늘은 ");
                for (int i = 0; i < keyword.size(); i++) {
                    comment.append(keyword.get(i));
                    if (i == keyword.size() - 1)
                        comment.append(" 감정을 느꼈습니다.");
                    else
                        comment.append(", ");
                }
                if (keyword.isEmpty()) {
                    comment.append("선택된 특이사항이 없습니다.");
                }
            }
            if (type.equals("exercise")) {
                comment.append("오늘은 ");
                for (int i = 0; i < keyword.size(); i++) {
                    comment.append(keyword.get(i));
                    if (i == keyword.size() - 1)
                        comment.append(" 신체활동을 했습니다.");
                    else
                        comment.append(", ");
                }
                if (keyword.isEmpty()) {
                    comment.append("선택된 활동이 없습니다.");
                }
            }
            return comment.toString();
        }

        private Member getMember(CustomUserDetails userInfo) {
            return memberRepository.findById(userInfo.getMember().getId())
                    .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        }
    }
