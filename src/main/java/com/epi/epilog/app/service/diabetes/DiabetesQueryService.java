package com.epi.epilog.app.service.diabetes;

import com.epi.epilog.app.domain.log.Log;
import com.epi.epilog.app.domain.member.Member;
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
import java.util.*;
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
        List<Log> logs = diabetesRepository.findAllByDateAndMember(date, newMember);

        List<DiabetesResponseDto.DiabetesBloodSugar> bloodSugars = new ArrayList<>();

        if (logs != null){
            bloodSugars = logs.stream().map(diabet ->
                    DiabetesResponseDto.DiabetesBloodSugar
                            .builder()
                            .bloodSugar(diabet.getBloodSugar())
                            .occurrenceType(diabet.getOccurrenceType())
                            .build()).collect(Collectors.toList());

            bloodSugars.sort(new CustomBloodSugarComparator());
        }

        return DiabetesResponseDto.BloodSugarTodayResponse.builder()
                .total(bloodSugars.size())
                .date(date)
                .diabetes(bloodSugars)
                .build();
    }
}