package com.epi.epilog.app.controller;

import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.dto.DiabetesRequestDto;
import com.epi.epilog.app.dto.DiabetesResponseDto;
import com.epi.epilog.app.service.logs.DiabetesCommandService;
import com.epi.epilog.app.service.logs.DiabetesQueryService;
import com.epi.epilog.global.exception.ErrorResponse;
import com.epi.epilog.global.utils.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/diabetes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Diabetes", description = "당뇨 일지 관련 API(워치 전용)")
public class DiabetesController {
    private final DiabetesCommandService diabetesCommandService;
    private final DiabetesQueryService diabetesQueryService;

    @Operation(summary = "워치 일 별 혈당 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "워치 일 별 혈당 목록 조회 성공"),
    })
    @GetMapping("/bloodsugars")
    public DiabetesResponseDto.BloodSugarTodayResponse bloodSugarList(
            @RequestParam(value = "date", required = false) LocalDate date) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return diabetesQueryService.showBloodSugarList(userDetails.getMember(), date != null ? date : LocalDate.now());
    }

    @Operation(summary = "워치 혈당 입력")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "워치 혈당 입력 성공"),
            @ApiResponse(responseCode = "404", description = "워치 일 별 혈당 목록 조회 실패 - 유저 조회 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "워치 일 별 혈당 목록 조회 실패 - 최대 입력 개수 초과", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/bloodsugar")
    public ResponseEntity<CommonResponseDto.CommonResponse> createBloodSugar(
            @RequestBody @Valid DiabetesRequestDto.BloodSugarRequest form) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        CommonResponseDto.CommonResponse bloodSugar = diabetesCommandService
                .createBloodSugar(form, userDetails.getMember());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bloodSugar);
    }
}
