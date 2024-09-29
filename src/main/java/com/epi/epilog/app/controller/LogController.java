package com.epi.epilog.app.controller;

import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.dto.LogsRequestDto;
import com.epi.epilog.app.dto.LogsResponseDto;
import com.epi.epilog.app.dto.PdfData;
import com.epi.epilog.app.service.fall.PdfService;
import com.epi.epilog.app.service.logs.LogCommandService;
import com.epi.epilog.app.service.logs.LogQueryService;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import com.epi.epilog.global.exception.ErrorResponse;
import com.epi.epilog.global.utils.CustomUserDetails;
import com.epi.epilog.global.utils.DateTimeConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@Tag(name = "Log", description = "일지 관련 API")
public class LogController {
    private final LogQueryService logQueryService;
    private final LogCommandService logCommandService;
    private final PdfService pdfService;

    @Operation(summary = "월 별 일지 개수 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "월 별 일지 개수 조회 성공"),
    })
    @GetMapping("/count")
    public LogsResponseDto.MonthLogsCount monthLogsCount(@RequestParam(value = "date", required = false) String date) {
        LocalDate queryDate;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (date == null) {
            date = DateTimeConverter.convertLocalDateToString(LocalDate.now());
        }

        queryDate = DateTimeConverter.convertToLocalDate(date);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        LogsResponseDto.MonthLogsCount logsCount = logQueryService.monthLogsCount(queryDate, userDetails.getMember());
        return logsCount;
    }

    @Operation(summary = "일 별 일지 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일 별 일지 목록 조회 성공"),
    })
    @GetMapping("")
    public LogsResponseDto.DayLogsList dayLogList(@RequestParam(value = "date", required = false) String date) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (date == null) {
            date = DateTimeConverter.convertLocalDateToString(LocalDate.now());
        }

        LocalDate queryDate = DateTimeConverter.convertToLocalDate(date);

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return logQueryService.dayLogList(customUserDetails, queryDate);
    }

    @Operation(summary = "일 별/식 전후 평균 혈당 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일 별/식 전후 평균 혈당 조회 성공"),
    })
    @GetMapping("/bloodsugar/average")
    public LogsResponseDto.DayAvgBloodSugar dayAvgBloodSugar(@RequestParam(value = "date", required = false) String date) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (date == null) {
            date = DateTimeConverter.convertLocalDateToString(LocalDate.now());
        }
        LocalDate queryDate = DateTimeConverter.convertToLocalDate(date);
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        return logQueryService.dayAvgBloodSugar(customUserDetails, queryDate);
    }

    @Operation(summary = "일 별 혈당 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일 별 혈당 목록 조회 성공"),
    })
    @GetMapping("/bloodsugar")
    public LogsResponseDto.DayBloodSugarList getDayBloodSugarList(@RequestParam(value = "date", required = false) String date) {
        if (date == null) {
            date = DateTimeConverter.convertLocalDateToString(LocalDate.now());
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return logQueryService.dayBloodSugarList(((CustomUserDetails) authentication.getPrincipal()), date);
    }

    @Operation(summary = "월 별 체중, 체지방률 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "월 별 체중, 체지방률 목록 조회 성공"),
    })
    @GetMapping("/weight")
    public LogsResponseDto.MonthWeightList getMonthWeightList(@RequestParam(value = "date", required = false) String date) {
        if (date == null) {
            date = DateTimeConverter.convertLocalDateToString(LocalDate.now());
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return logQueryService.getMonthWeightList(date, ((CustomUserDetails) authentication.getPrincipal()));
    }

    @Operation(summary = "일지 등록")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "일지 등록 성공"),
            @ApiResponse(responseCode = "404", description = "일지 등록 실패 - 유저 찾기 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("")
    public ResponseEntity<CommonResponseDto.CommonResponse> createLog(@RequestBody LogsRequestDto.LogCreateForm form) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CommonResponseDto.CommonResponse log = logCommandService.createLog(((CustomUserDetails) authentication.getPrincipal()), form);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(log);
    }

    @Operation(summary = "일지 상세 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일지 상세 조회 성공"),
    })
    @GetMapping("/{logId}")
    public LogsResponseDto.DetailAllLog detailLog(@PathVariable("logId") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return logQueryService.getDetailLog(((CustomUserDetails) authentication.getPrincipal()), id);
    }

    @Operation(summary = "PDF 변환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "PDF 변환 성공"),
            @ApiResponse(responseCode = "400", description = "PDF 변환 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/convert")
    public ResponseEntity<InputStreamResource> createdPDF(@RequestParam(value = "start", required = true) LocalDate start,
                                                          @RequestParam(value = "end", required = true) LocalDate end) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<PdfData> entries = logQueryService.generatedPdfEntries(((CustomUserDetails) authentication.getPrincipal()), start, end);
        try {
            ByteArrayOutputStream baos = pdfService.createPdf("diabetes_log", entries);

        } catch (Exception e) {
            throw new ApiException(ErrorCode.FAIL_TO_CONVERSION_PDF);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=diabetes_log.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(new ByteArrayInputStream(baos.toByteArray())));
    }
}
