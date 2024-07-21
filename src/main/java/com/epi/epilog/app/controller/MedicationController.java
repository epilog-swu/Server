package com.epi.epilog.app.controller;

import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.dto.MedicationRequestDto;
import com.epi.epilog.app.dto.MedicationResponseDto;
import com.epi.epilog.app.service.checklist.MedicationCommandService;
import com.epi.epilog.app.service.checklist.MedicationQueryService;
import com.epi.epilog.global.utils.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medications")
@RequiredArgsConstructor
public class MedicationController {
    private final MedicationQueryService medicationQueryService;
    private final MedicationCommandService medicationCommandService;

    /**
     * 복용약 상세 조회
     */
    @GetMapping("/{mcId}")
    public MedicationResponseDto.GetMedicationForm getMedicationDetails(@PathVariable("mcId")Long medicationId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userInfo = (CustomUserDetails) authentication.getPrincipal();
        return medicationQueryService.getMedicationDetails(medicationId, userInfo);
    }

    /**
     * 복용약 추가
     */
    @PostMapping("")
    public CommonResponseDto.CommonResponse addMedication(@RequestBody MedicationRequestDto.MedicationAddedForm form){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userInfo = (CustomUserDetails) authentication.getPrincipal();
        return medicationCommandService.addMedication(userInfo, form);
    }

    /**
     * 복용약 수정
     */

    /**
     * 복용약 삭제
     */
}
