package com.epi.epilog.app.service.logs;

import com.epi.epilog.app.domain.logs.LogExercise;
import com.epi.epilog.app.domain.logs.LogMood;
import com.epi.epilog.app.domain.logs.Log;
import com.epi.epilog.app.domain.member.Member;
import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.dto.LogsRequestDto;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LogCommandService {
    private final DiabetesCommandService diabetesCommandService;
    private final MemberRepository memberRepository;
    private final LogMoodRepository logMoodRepository;
    private final LogExerciseRepository logExerciseRepository;
    private final LogRepository logRepository;

    /**
     * 일지 등록
     * @param principal 유저 정보
     * @param form
     * @return
     */
    @Transactional
    public CommonResponseDto.CommonResponse createLog(CustomUserDetails principal, LogsRequestDto.LogCreateForm form) {
        try {
            Member member = memberRepository.findById(principal.getMember().getId())
                    .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

            Log log = Log.builder()
                    .member(member)
                    .occurrenceType(form.getOccurenceType())
                    .title(diabetesCommandService.createTitle(member, DateTimeConverter.convertToLocalDate(form.getDate()), form.getOccurenceType()))
                    .date(DateTimeConverter.convertToLocalDate(form.getDate()))
                    .occurrenceType(form.getOccurenceType())
                    .isFall(false)
                    .isBloodSugar(form.getBloodSugar() != null)
                    .isBloodPressure(form.getSystolicBloodPressure() != null
                            || form.getDiastolicBloodPressure() != null
                            || form.getHeartRate() != null)
                    .isWeight(form.getWeight() != null
                            || form.getBodyFatPercentage() != null
                            || form.getBodyPhoto() != null)
                    .isExercise(form.getExercise() != null && !form.getExercise().isEmpty())
                    .isMood(form.getMood() != null && !form.getMood().isEmpty())
                    .bloodSugar(form.getBloodSugar() != null ? form.getBloodSugar() : null)
                    .systolicBloodPressure(form.getSystolicBloodPressure() != null ? form.getSystolicBloodPressure() : null)
                    .diastolicBloodPressure(form.getDiastolicBloodPressure() != null ? form.getDiastolicBloodPressure() : null)
                    .heartRate(form.getHeartRate() != null ? form.getHeartRate() : null)
                    .weight(form.getWeight() != null ? form.getWeight() : null)
                    .bodyFatPercentage(form.getBodyFatPercentage() != null ? form.getBodyFatPercentage() : null)
                    .bodyPhoto(form.getBodyPhoto() != null ? form.getBodyPhoto() : null)
                    .build();

            logRepository.save(log);

            if (form.getMood() != null && !form.getMood().isEmpty()) {
                List<LogMood> logMoods = form.getMood().stream()
                        .map(moodRequest -> LogMood.builder()
                                .log(log)
                                .type(moodRequest.getType() != null ? moodRequest.getType() : "")
                                .details(moodRequest.getDetails())
                                .detailsState(moodRequest.getType().equals("직접입력")
                                        ?true : false)
                                .build())
                        .collect(Collectors.toList());
                logMoodRepository.saveAll(logMoods);
            }

            if (form.getExercise() != null && !form.getExercise().isEmpty()) {
                List<LogExercise> logExerciseList = form.getExercise().stream()
                        .map(exercise -> LogExercise.builder()
                                .log(log)
                                .type(exercise.getType() != null ? exercise.getType() : null)
                                .details(exercise.getDetails())
                                .detailsState(exercise.getType().equals("직접입력")
                                        ?true : false)
                                .build())
                        .collect(Collectors.toList());
                logExerciseRepository.saveAll(logExerciseList);
            }
            return CommonResponseDto.CommonResponse.builder().message("작성에 성공했습니다").success(true).build();
        } catch (Exception e){
            throw new ApiException(ErrorCode.USER_NOT_FOUND, e);
        }
    }
}
