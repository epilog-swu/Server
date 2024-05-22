package com.epi.epilog.app.controller;

import com.epi.epilog.app.domain.Member;
import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.dto.CustomUserInfoDto;
import com.epi.epilog.app.dto.DiabetesRequestDto;
import com.epi.epilog.app.dto.DiabetesResponseDto;
import com.epi.epilog.app.repository.DiabetesRepository;
import com.epi.epilog.app.repository.MemberRepository;
import com.epi.epilog.app.service.DiabetesCommandService;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import com.epi.epilog.global.exception.ErrorResponse;
import com.epi.epilog.global.utils.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diabetes")
@RequiredArgsConstructor
@Slf4j
public class DiabetesController {
    private final DiabetesCommandService diabetesCommandService;

//    @GetMapping("/bloodsugar/today")
//    public DiabetesResponseDto.BloodSugarTodayResponse bloodSugarToday(){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//    }

    @PostMapping("/bloodsugar")
    public CommonResponseDto.CommonResponse createBloodSugar(@RequestBody @Valid DiabetesRequestDto.BloodSugarRequest form){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return diabetesCommandService.createBloodSugar(form, userDetails.getMember());
        }

        throw new ApiException(ErrorCode.INVALID_TOKEN);
    }

}
