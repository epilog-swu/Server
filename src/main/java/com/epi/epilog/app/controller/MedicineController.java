package com.epi.epilog.app.controller;

import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.dto.MedicationResponseDto;
import com.epi.epilog.app.service.checklist.MedicineCommandService;
import com.epi.epilog.app.service.checklist.MedicineQueryService;
import com.epi.epilog.global.utils.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@Tag(name = "Medicine", description = "복용약 체크리스트 API")
public class MedicineController {
    private final MedicineCommandService medicineCommandService;
    private final MedicineQueryService medicineQueryService;

    @Operation(summary = "일 별 복약 체크리스트 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일 별 복약 체크리스트 목록 조회 성공")
    })
    @GetMapping("")
    public MedicationResponseDto.ChecklistDto showMedicines(@RequestParam(value = "date", required = false) LocalDate date) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return medicineQueryService.medicineChecklist(date != null ? date : LocalDate.now(), userDetails.getMember());
    }

    /**
     * 복약 체크리스트 상태 수정
     *
     * @param id
     * @param form
     * @return
     */
    @Operation(summary = "복약 체크리스트 상태 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "복약 체크리스트 상태 수정 성공")
    })
    @PatchMapping("/{chklstId}")
    public CommonResponseDto.CommonResponse medicineCheck(@PathVariable("chklstId") Long id, @RequestBody @Valid MedicationResponseDto.MedicineChecklistUpdateDto form) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userInfo = (CustomUserDetails) authentication.getPrincipal();

        return medicineCommandService.medicineCheck(id, form, userInfo);
    }
}
