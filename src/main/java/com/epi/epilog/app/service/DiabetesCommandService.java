package com.epi.epilog.app.service;

import com.epi.epilog.app.domain.Diabetes;
import com.epi.epilog.app.domain.Member;
import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.dto.CustomUserInfoDto;
import com.epi.epilog.app.dto.DiabetesRequestDto;
import com.epi.epilog.app.repository.DiabetesRepository;
import com.epi.epilog.app.repository.MemberRepository;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DiabetesCommandService {
    private final DiabetesRepository diabetesRepository;
    private final MemberRepository memberRepository;

    public CommonResponseDto.CommonResponse createBloodSugar(
            DiabetesRequestDto.BloodSugarRequest form, CustomUserInfoDto member) {
        Member mem = memberRepository.findById(member.getId()).orElseThrow(()->
                new ApiException(ErrorCode.USER_NOT_FOUND));
        // create diabetes
        Diabetes diabet = Diabetes.builder()
                .member(mem)
                .date(LocalDate.now())
                .occurrenceType(form.getOccurrenceType())
                .title(form.getOccurrenceType().toString())
                .bloodSugar(form.getBloodSugar())
                .build();
        diabetesRepository.save(diabet);

        // return form
        return CommonResponseDto.CommonResponse.builder()
                .message("일지가 추가됐습니다.")
                .success(true)
                .build();
    }
}
