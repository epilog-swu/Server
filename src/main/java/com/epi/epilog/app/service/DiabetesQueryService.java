package com.epi.epilog.app.service;

import com.epi.epilog.app.converter.DiabetesConverter;
import com.epi.epilog.app.domain.Diabetes;
import com.epi.epilog.app.domain.Member;
import com.epi.epilog.app.dto.CustomUserInfoDto;
import com.epi.epilog.app.dto.DiabetesResponseDto;
import com.epi.epilog.app.repository.DiabetesRepository;
import com.epi.epilog.app.repository.MemberRepository;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiabetesQueryService {
    private final DiabetesRepository diabetesRepository;
    private final MemberRepository memberRepository;

    public DiabetesResponseDto.BloodSugarTodayResponse showBloodSugarList(CustomUserInfoDto member, LocalDate date) {
        Member newMember = memberRepository.findById(member.getId())
                .orElseThrow(()->new ApiException(ErrorCode.USER_NOT_FOUND));
        List<Diabetes> diabetes = diabetesRepository.findAllByDateAndMember(date, newMember);

        // 정렬 로직

        List<DiabetesResponseDto.DiabetesBloodSugar> bloodSugars = new ArrayList<>();

        if (diabetes != null){
            bloodSugars = diabetes.stream().map(diabet ->
                    DiabetesResponseDto.DiabetesBloodSugar
                            .builder()
                            .bloodSugar(diabet.getBloodSugar())
                            .occurrenceType(diabet.getOccurrenceType())
                            .build()).collect(Collectors.toList());
        }

        return DiabetesResponseDto.BloodSugarTodayResponse.builder()
                .total(bloodSugars.size())
                .date(date)
                .diabetes(bloodSugars)
                .build();
    }
}
