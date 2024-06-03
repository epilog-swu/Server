package com.epi.epilog.app.controller;

import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.dto.DiabetesRequestDto;
import com.epi.epilog.app.dto.DiabetesResponseDto;
import com.epi.epilog.app.service.diabetes.DiabetesCommandService;
import com.epi.epilog.app.service.diabetes.DiabetesQueryService;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import com.epi.epilog.global.utils.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/diabetes")
@RequiredArgsConstructor
@Slf4j
public class DiabetesController {
    private final DiabetesCommandService diabetesCommandService;
    private final DiabetesQueryService diabetesQueryService;

    @GetMapping("/bloodsugars")
    public DiabetesResponseDto.BloodSugarTodayResponse bloodSugarList(@RequestParam(value = "date", required = false) LocalDate date){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return diabetesQueryService.showBloodSugarList(userDetails.getMember(), date!=null?date:LocalDate.now());
        }
        throw new ApiException(ErrorCode.INVALID_TOKEN);
    }

    @PostMapping("/bloodsugar")
    public CommonResponseDto.CommonResponse createBloodSugar(@RequestBody @Valid DiabetesRequestDto.BloodSugarRequest form){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return diabetesCommandService.createBloodSugar(form, userDetails.getMember());
        }

        throw new ApiException(ErrorCode.INVALID_TOKEN);
    }

}
