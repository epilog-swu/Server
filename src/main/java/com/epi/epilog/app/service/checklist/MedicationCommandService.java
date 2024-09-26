package com.epi.epilog.app.service.checklist;

import com.epi.epilog.app.domain.enums.WeekType;
import com.epi.epilog.app.domain.medication.Medication;
import com.epi.epilog.app.domain.medication.MedicationCheckList;
import com.epi.epilog.app.domain.enums.MedicationStatus;
import com.epi.epilog.app.domain.member.Member;
import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.dto.MedicationRequestDto;
import com.epi.epilog.app.repository.MedicationCheckListRepository;
import com.epi.epilog.app.repository.MedicationRepository;
import com.epi.epilog.app.repository.MemberRepository;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import com.epi.epilog.global.utils.CustomUserDetails;
import com.epi.epilog.global.utils.DateTimeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicationCommandService {
    private final MedicationRepository medicationRepository;
    private final MemberRepository memberRepository;
    private final MedicationCheckListRepository medicationCheckListRepository;

    /**
     * 복용약 생성
     *
     * @param userInfo
     * @param form
     * @return
     */
    @Transactional
    public CommonResponseDto.CommonResponse addMedication
    (CustomUserDetails userInfo, MedicationRequestDto.MedicationAddedForm form) {
        Member member = memberRepository.findById(userInfo.getMember().getId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        List<WeekType> weekTypeList = form.getWeeks().stream().map(week -> {
                    if (week.equals("월")) return WeekType.월;
                    if (week.equals("화")) return WeekType.화;
                    if (week.equals("수")) return WeekType.수;
                    if (week.equals("목")) return WeekType.목;
                    if (week.equals("금")) return WeekType.금;
                    if (week.equals("토")) return WeekType.토;
                    if (week.equals("일")) return WeekType.일;
                    throw new ApiException(ErrorCode.INVALID_FORMAT_ERROR);
                }
        ).collect(Collectors.toList());

        Medication medication = Medication.builder()
                .medicationName(form.getMedicationName())
                .times(form.getTimes())
                .startDate(form.getStartDate())
                .endDate(form.getEndDate())
                .endless(form.isEndless())
                .isAlarm(form.isAlarm())
                .weeks(weekTypeList)
                .effectiveness(form.getEffectiveness())
                .precautions(form.getPrecautions())
                .storageMethod(form.getStorageMethod())
                .member(member)
                .build();

        Medication saveMedication = medicationRepository.save(medication);

        // 복용약 추가 시 체크리스트 추가
        createAutoMedicationChecklist(true, saveMedication);

        return CommonResponseDto.CommonResponse.builder()
                .success(true)
                .message("추가되었습니다.")
                .build();
    }

    /**
     * 복용약 수정
     *
     * @param userInfo
     * @param medicationId
     * @param form
     * @return
     */
    @Transactional
    public CommonResponseDto.CommonResponse patchMedication(CustomUserDetails userInfo,
                                                            Long medicationId,
                                                            MedicationRequestDto.MedicationAddedForm form) {
        Medication medication = medicationRepository.findById(medicationId)
                .orElseThrow(() -> new ApiException(ErrorCode.MEDICATION_NOT_FOUND));

        Member member = memberRepository.findById(userInfo.getMember().getId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        if (medication.getMember() != member) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        Medication.MedicationBuilder medicationBuilder = medication.toBuilder();

        if (form.getMedicationName() != null) {
            medicationBuilder.medicationName(form.getMedicationName());
        }
        if (form.getTimes() != null) {
            medicationBuilder.times(form.getTimes());
        }
        if (form.getStartDate() != null) {
            medicationBuilder.startDate(form.getStartDate());
        }
        if (form.getEndDate() != null) {
            medicationBuilder.endDate(form.getEndDate());
        }
        medicationBuilder.endless(form.isEndless());
        medicationBuilder.isAlarm(form.isAlarm());
        if (form.getWeeks() != null) {
            List<WeekType> weekTypeList = form.getWeeks().stream().map(week -> {
                switch (week) {
                    case "월":
                        return WeekType.월;
                    case "화":
                        return WeekType.화;
                    case "수":
                        return WeekType.수;
                    case "목":
                        return WeekType.목;
                    case "금":
                        return WeekType.금;
                    case "토":
                        return WeekType.토;
                    case "일":
                        return WeekType.일;
                    default:
                        throw new ApiException(ErrorCode.INVALID_FORMAT_ERROR);
                }
            }).collect(Collectors.toList());
            medicationBuilder.weeks(weekTypeList);
        }
        if (form.getEffectiveness() != null) {
            medicationBuilder.effectiveness(form.getEffectiveness());
        }
        if (form.getPrecautions() != null) {
            medicationBuilder.precautions(form.getPrecautions());
        }
        if (form.getStorageMethod() != null) {
            medicationBuilder.storageMethod(form.getStorageMethod());
        }

        Medication updatedMedication = medicationBuilder.build();
        medicationRepository.save(updatedMedication);

        return CommonResponseDto.CommonResponse.builder()
                .success(true)
                .message("수정되었습니다.")
                .build();
    }

    /**
     * 복용약 삭제
     *
     * @param userInfo
     * @param medicationId
     * @return
     */
    @Transactional
    public CommonResponseDto.CommonResponse deleteMedication(CustomUserDetails userInfo, Long medicationId) {
        Member member = memberRepository.findById(userInfo.getMember().getId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Medication medication = medicationRepository.findById(medicationId)
                .orElseThrow(() -> new ApiException(ErrorCode.MEDICATION_NOT_FOUND));

        if (medication.getMember() != member)
            throw new ApiException(ErrorCode.UNAUTHORIZED);

        List<MedicationCheckList> checkLists = medicationCheckListRepository.findAllByMedication(medication);
        for (MedicationCheckList checkList : checkLists) {
            checkList.deleteMedication();
            if (checkList.getGoalTime().isAfter(LocalDateTime.now()))
                medicationCheckListRepository.delete(checkList);
        }

        medicationRepository.delete(medication);

        return CommonResponseDto.CommonResponse.builder()
                .success(true)
                .message("삭제되었습니다.")
                .build();
    }

    /**
     * 복약 체크리스트 자동 생성
     * create flag -> true일 경우 복용약 추가 / false일 경우 스케줄러
     *
     * @param create 생성 flag
     */
    @Transactional
    public void createAutoMedicationChecklist(boolean create, Medication newMedication) {
        if (create == true) {
            LocalDate today = LocalDate.now();

            List<MedicationCheckList> checkLists = new ArrayList<>();

            for (int i = 0; i < 7; i++) {
                LocalDate targetDate = today.plusDays(i);

                if (targetDate.isAfter(newMedication.getEndDate()))
                    return;

                WeekType todayWeekType = WeekType.valueOf(targetDate.getDayOfWeek()
                        .getDisplayName(TextStyle.SHORT, Locale.KOREAN)
                        .substring(0, 1));

                if (newMedication.getWeeks().contains(todayWeekType)) {
                    newMedication.getTimes().forEach(time -> {
                        MedicationCheckList checkList = MedicationCheckList.builder()
                                .medication(newMedication)
                                .isComplete(false)
                                .title((time.getMinute() != 0 ?
                                        time.format(DateTimeConverter.krTimeFormatter) :
                                        time.format(DateTimeConverter.krShortTimeFormatter))
                                        + " " + newMedication.getMedicationName())
                                .goalTime(targetDate.atTime(time))
                                .medicationStatus(MedicationStatus.상태없음)
                                .build();

                        checkLists.add(checkList);
                    });
                }
            }
            medicationCheckListRepository.saveAll(checkLists);
        } else {
            List<Medication> all = medicationRepository.findAll();
            if (!all.isEmpty()) {
                List<MedicationCheckList> collect = all.stream()
                        .filter(medication -> medication.isEndless() == true
                                || medication.getEndDate().isAfter(LocalDate.now().plusDays(8)))
                        .filter(medication -> medication.getWeeks() == null
                                || (medication.getWeeks() != null
                                && medication.getWeeks().contains(LocalDate.now().getDayOfWeek())))
                        .flatMap(medication -> medication.getTimes().stream()
                                .map(times -> MedicationCheckList.builder()
                                        .medication(medication)
                                        .isComplete(false)
                                        .title((times.getMinute() != 0 ?
                                                times.format(DateTimeConverter.krTimeFormatter) :
                                                times.format(DateTimeConverter.krShortTimeFormatter))
                                                + " " + medication.getMedicationName())
                                        .goalTime(LocalDate.now().plusDays(8).atTime(times))
                                        .medicationStatus(MedicationStatus.상태없음)
                                        .build()))
                        .collect(Collectors.toList());
                medicationCheckListRepository.saveAll(collect);
            }
        }
    }
}
