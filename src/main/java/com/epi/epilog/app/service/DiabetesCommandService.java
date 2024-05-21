package com.epi.epilog.app.service;

import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.dto.CustomUserInfoDto;
import com.epi.epilog.app.dto.DiabetesRequestDto;
import com.epi.epilog.app.repository.DiabetesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiabetesCommandService {
    private final DiabetesRepository diabetesRepository;

    public CommonResponseDto.CommonResponse createBloodSugar(
            DiabetesRequestDto.BloodSugarRequest form, CustomUserInfoDto member) {

    }
}
