package com.epi.epilog.app.controller;

import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.dto.MedicationRequestDto;
import com.epi.epilog.app.dto.MedicationResponseDto;
import com.epi.epilog.app.service.checklist.MedicationCommandService;
import com.epi.epilog.app.service.checklist.MedicationQueryService;
import com.epi.epilog.global.exception.ErrorResponse;
import com.epi.epilog.global.utils.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/medications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Medication", description = "복약 관리 API")
public class MedicationController {
    private final MedicationQueryService medicationQueryService;
    private final MedicationCommandService medicationCommandService;

    @Operation(summary = "복용약 상세 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "복용약 상세 조회 성공"),
    })
    @GetMapping("/{mcId}")
    public MedicationResponseDto.GetMedicationForm getMedicationDetails(@PathVariable("mcId") Long medicationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userInfo = (CustomUserDetails) authentication.getPrincipal();
        return medicationQueryService.getMedicationDetails(medicationId, userInfo);
    }

    @Operation(summary = "복용약 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "복용약 추가 성공"),
            @ApiResponse(responseCode = "404", description = "복용약 추가 실패 - 유저 조회 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "복용약 추가 실패 - 유효하지 않은 포맷", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("")
    public ResponseEntity<CommonResponseDto.CommonResponse> addMedication(
            @RequestBody MedicationRequestDto.MedicationAddedForm form) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userInfo = (CustomUserDetails) authentication.getPrincipal();

        CommonResponseDto.CommonResponse commonResponse = medicationCommandService.addMedication(userInfo, form);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commonResponse);
    }

    @Operation(summary = "복용약 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "복용약 수정 성공"),
            @ApiResponse(responseCode = "404", description = "복용약 수정 실패 - 약 조회 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "복용약 수정 실패 - 유저 조회 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "복용약 수정 실패 - 유저 인증 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "복용약 수정 실패 - 유효하지 않은 포맷", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PatchMapping("/{mcId}")
    public CommonResponseDto.CommonResponse patchMedication(@PathVariable("mcId") Long medicationId, @RequestBody MedicationRequestDto.MedicationAddedForm form) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userInfo = (CustomUserDetails) authentication.getPrincipal();

        return medicationCommandService.patchMedication(userInfo, medicationId, form);
    }

    @Operation(summary = "복용약 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "복용약 삭제 성공")
    })
    @DeleteMapping("/{mcId}")
    public ResponseEntity<Void> deleteMedication(@PathVariable("mcId") Long medicationId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userInfo = (CustomUserDetails) authentication.getPrincipal();

        medicationCommandService.deleteMedication(userInfo, medicationId);
        return ResponseEntity.noContent().build();
    }
}
