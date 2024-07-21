package com.epi.epilog.app.controller;

import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.dto.MealsResponseDto;
import com.epi.epilog.app.dto.MedicineResponseDto;
import com.epi.epilog.app.service.checklist.MedicineCommandService;
import com.epi.epilog.app.service.checklist.MedicineQueryService;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import com.epi.epilog.global.utils.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Security;
import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/medicines")
public class MedicineController {
    private final MedicineCommandService medicineCommandService;
    private final MedicineQueryService medicineQueryService;

    @GetMapping("")
    public MedicineResponseDto.ChecklistDto showMedicines(@RequestParam(value = "date", required = false)LocalDate date) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return medicineQueryService.medicineChecklist(date!=null?date:LocalDate.now(), userDetails.getMember());
        } catch(Exception e){
            throw new ApiException(ErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * 유저 검증 절차 추가하기
     * @param id
     * @param form
     * @return
     */
    @PatchMapping("/{chklstId}")
    public CommonResponseDto.CommonResponse medicineCheck(@PathVariable("chklstId")Long id, @RequestBody @Valid MedicineResponseDto.MedicineChecklistUpdateDto form){
        try {
            return medicineCommandService.medicineCheck(id, form);
        } catch(Exception e){
            throw new ApiException(ErrorCode.INVALID_TOKEN);
        }
    }

}
