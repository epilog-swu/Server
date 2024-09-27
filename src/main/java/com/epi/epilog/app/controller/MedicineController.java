package com.epi.epilog.app.controller;

import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.dto.MedicationResponseDto;
import com.epi.epilog.app.service.checklist.MedicineCommandService;
import com.epi.epilog.app.service.checklist.MedicineQueryService;
import com.epi.epilog.global.utils.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/medicines")
@RequiredArgsConstructor
public class MedicineController {
    private final MedicineCommandService medicineCommandService;
    private final MedicineQueryService medicineQueryService;

    /**
     * 일별 복약 체크리스트 목록 조회
     * @param date
     * @return
     */
    @GetMapping("")
    public MedicationResponseDto.ChecklistDto showMedicines(@RequestParam(value = "date", required = false)LocalDate date) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return medicineQueryService.medicineChecklist(date!=null?date:LocalDate.now(), userDetails.getMember());
    }

    /**
     * 복약 체크리스트 상태 수정
     * @param id
     * @param form
     * @return
     */
    @PatchMapping("/{chklstId}")
    public CommonResponseDto.CommonResponse medicineCheck(@PathVariable("chklstId")Long id, @RequestBody @Valid MedicationResponseDto.MedicineChecklistUpdateDto form){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userInfo = (CustomUserDetails) authentication.getPrincipal();
        return medicineCommandService.medicineCheck(id, form, userInfo);
    }
}
