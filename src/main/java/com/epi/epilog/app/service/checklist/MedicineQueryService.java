package com.epi.epilog.app.service.checklist;

import com.epi.epilog.app.domain.Member;
import com.epi.epilog.app.dto.CustomUserInfoDto;
import com.epi.epilog.app.dto.MedicineResponseDto;
import com.epi.epilog.app.repository.MedicineLogRepository;
import com.epi.epilog.app.repository.MemberRepository;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicineQueryService {
    private final MemberRepository memberRepository;
    private final MedicineLogRepository medicineLogRepository;
    public MedicineResponseDto.ChecklistDto medicineChecklist(LocalDate date, CustomUserInfoDto memberDto) {
        Member member = memberRepository.findById(memberDto.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));



    }
}
