package com.epi.epilog.app.service.checklist;

import com.epi.epilog.app.domain.medication.Medication;
import com.epi.epilog.app.domain.medication.MedicationCheckList;
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
    public MedicationResponseDto.ChecklistDto medicineChecklist(LocalDate date, CustomUserInfoDto memberDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H시 mm분");
        DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("H시");

        Member member = memberRepository.findById(memberDto.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        List<Medication> medicationList = medicationRepository.findAllByMemberOrderByCreatedAt(member);

        List<MedicationCheckList> medicationCheckLists = medicationCheckListRepository
                .findAllByMemberAndGoalTimeOrderByGoalTimeAsc(member, date.atStartOfDay(), date.atTime(LocalTime.MAX));

        List<MedicationResponseDto.ChecklistStateDto> lists = new ArrayList<>();
        if (!medicationCheckLists.isEmpty()){
            lists = medicationCheckLists.stream().map(medicine -> MedicationResponseDto.ChecklistStateDto.builder()
                            .id(medicine.getId())
                            .goalTime(medicine.getGoalTime().format((DateTimeConverter.timeFormatter)))
                            .title((medicine.getGoalTime().format(medicine.getGoalTime().getMinute()==0?hourFormatter:formatter)) + " " + medicine.getMedication().getMedicationName())
                            .isComplete(medicine.getIsComplete())
                            .state(medicine.getMedicationStatus().toString())
                            .build()
            ).collect(Collectors.toList()
            );
        }

        return MedicationResponseDto.ChecklistDto.builder()
                .date(date)
                .medicationId(!medicationList.isEmpty() ? medicationList.get(0).getId() : null)
                .checklist(lists)
                .build();
    }
}
