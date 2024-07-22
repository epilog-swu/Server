package com.epi.epilog.app.service.checklist;

import com.epi.epilog.app.domain.medication.Medication;
import com.epi.epilog.app.domain.medication.MedicationCheckList;
import com.epi.epilog.app.domain.medication.MedicationStatus;
import com.epi.epilog.app.domain.member.Member;
import com.epi.epilog.app.dto.CustomUserInfoDto;
import com.epi.epilog.app.dto.MedicationResponseDto;
import com.epi.epilog.app.repository.MedicationCheckListRepository;
import com.epi.epilog.app.repository.MedicationRepository;
import com.epi.epilog.app.repository.MemberRepository;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import com.epi.epilog.global.utils.DateTimeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicineQueryService {
    private final MemberRepository memberRepository;
    private final MedicationRepository medicationRepository;
    private final MedicationCheckListRepository medicationCheckListRepository;

    /**
     * 일별 복약 체크리스트 목록 조회
     * @param date
     * @param memberDto
     * @return
     */
    public MedicationResponseDto.ChecklistDto medicineChecklist(LocalDate date, CustomUserInfoDto memberDto) {
        Member member = memberRepository.findById(memberDto.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        List<Medication> medicationList = medicationRepository.findAllByMemberOrderByCreatedAt(member);

        List<MedicationCheckList> medicationCheckLists = medicationCheckListRepository
                .findAllByMemberAndGoalTimeOrderByGoalTimeAsc(member, date.atStartOfDay(), date.atTime(LocalTime.MAX));

        List<MedicationResponseDto.ChecklistStateDto> lists = medicationCheckLists.stream()
                .map(this::convertToChecklistStateDto)
                .collect(Collectors.toList());

        return MedicationResponseDto.ChecklistDto.builder()
                .date(date)
                .medicationId(medicationList.stream().findFirst().map(Medication::getId).orElse(null))
                .checklist(lists)
                .build();
    }

    private MedicationResponseDto.ChecklistStateDto convertToChecklistStateDto(MedicationCheckList medicine) {
        String formattedGoalTime = DateTimeConverter.formatTime(medicine.getGoalTime());
        String formattedActualTime = medicine.getActualTime() != null
                ? DateTimeConverter.formatTime(medicine.getActualTime())
                : null;

        String title = medicine.getActualTime() == null ?
                medicine.getTitle() :
                formattedActualTime + " " + medicine.getMedication().getMedicationName();

        return MedicationResponseDto.ChecklistStateDto.builder()
                .id(medicine.getId())
                .goalTime(medicine.getGoalTime().format((DateTimeConverter.timeFormatter)))
                .title(title)
                .medicationName(medicine.getMedication().getMedicationName())
                .time(formattedActualTime != null ? formattedActualTime : formattedGoalTime)
                .isComplete(medicine.getIsComplete())
                .state(medicine.getMedicationStatus().toString())
                .build();
    }
}
