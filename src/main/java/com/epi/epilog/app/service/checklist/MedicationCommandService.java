package com.epi.epilog.app.service.checklist;

import com.epi.epilog.app.domain.enums.WeekType;
import com.epi.epilog.app.domain.medication.Medication;
import com.epi.epilog.app.domain.member.Member;
import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.dto.MedicationRequestDto;
import com.epi.epilog.app.repository.MedicationRepository;
import com.epi.epilog.app.repository.MemberRepository;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import com.epi.epilog.global.utils.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicationCommandService {
    private final MedicationRepository medicationRepository;
    private final MemberRepository memberRepository;

    /**
     * 생성
     * @param userInfo
     * @param form
     * @return
     */
    @Transactional
    public CommonResponseDto.CommonResponse addMedication
            (CustomUserDetails userInfo, MedicationRequestDto.MedicationAddedForm form) {
        Member member = memberRepository.findById(userInfo.getMember().getId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        List<WeekType> weekTypeList = form.getWeeks().stream().map(week->{
            if(week.equals("월")) return WeekType.월;
            if(week.equals("화")) return WeekType.화;
            if(week.equals("수")) return WeekType.수;
            if(week.equals("목")) return WeekType.목;
            if(week.equals("금")) return WeekType.금;
            if(week.equals("토")) return WeekType.토;
            if(week.equals("일")) return WeekType.일;
            throw new ApiException(ErrorCode.INVALID_FORMAT_ERROR);}
            ).collect(Collectors.toList());

        Medication medication = Medication.builder()
                .medicationName(form.getMedicationName())
                .times(form.getTimes())
                .startDate(form.getStartDate())
                .endDate(form.getEndDate())
                .endless(form.getEndless())
                .isAlarm(form.getIsAlarm())
                .weeks(weekTypeList)
                .effectiveness(form.getEffectiveness())
                .precautions(form.getPrecautions())
                .storageMethod(form.getStorageMethod())
                .member(member)
                .build();

        medicationRepository.save(medication);

        return CommonResponseDto.CommonResponse.builder()
                .success(true)
                .message("추가되었습니다.")
                .build();
    }

    /**
     * 수정
     * @param userInfo
     * @param medicationId
     * @param form
     * @return
     */
    @Transactional
    public CommonResponseDto.CommonResponse patchMedication(CustomUserDetails userInfo, Long medicationId, MedicationRequestDto.MedicationAddedForm form) {
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
        if (form.getEndless() != null) {
            medicationBuilder.endless(form.getEndless());
        }
        if (form.getIsAlarm() != null) {
            medicationBuilder.isAlarm(form.getIsAlarm());
        }
        if (form.getWeeks() != null) {
            List<WeekType> weekTypeList = form.getWeeks().stream().map(week -> {
                switch (week) {
                    case "월": return WeekType.월;
                    case "화": return WeekType.화;
                    case "수": return WeekType.수;
                    case "목": return WeekType.목;
                    case "금": return WeekType.금;
                    case "토": return WeekType.토;
                    case "일": return WeekType.일;
                    default: throw new ApiException(ErrorCode.INVALID_FORMAT_ERROR);
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
}
