package com.epi.epilog.app.service.checklist;

import com.epi.epilog.app.domain.MedicineLog;
import com.epi.epilog.app.domain.Member;
import com.epi.epilog.app.dto.CustomUserInfoDto;
import com.epi.epilog.app.dto.MedicineResponseDto;
import com.epi.epilog.app.repository.MedicineLogRepository;
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
    private final MedicineLogRepository medicineLogRepository;
    public MedicineResponseDto.ChecklistDto medicineChecklist(LocalDate date, CustomUserInfoDto memberDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H시 mm분");
        DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("H시");

        Member member = memberRepository.findById(memberDto.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        List<MedicineLog> medicineLogs = medicineLogRepository
                .findAllByMemberAndGoalTime(member, date.atStartOfDay(), date.atTime(LocalTime.MAX));

        List<MedicineResponseDto.ChecklistStateDto> lists = new ArrayList<>();
        if (!medicineLogs.isEmpty()){
            lists = medicineLogs.stream().map(medicine -> MedicineResponseDto.ChecklistStateDto.builder()
                            .id(medicine.getId())
                            .goalTime(medicine.getGoalTime().format((DateTimeConverter.timeFormatter)))
                            .title(medicine.getGoalTime().format(medicine.getGoalTime().getMinute()==0?hourFormatter:formatter))
                            .isComplete(medicine.getIsComplete())
                            .state(medicine.getMedicationStatus().toString())
                            .build()
            ).collect(Collectors.toList()
            );
        }

        return MedicineResponseDto.ChecklistDto.builder()
                .date(date)
                .checklist(lists)
                .build();
    }
}
