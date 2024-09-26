package com.epi.epilog.app.controller;

import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.dto.LogsRequestDto;
import com.epi.epilog.app.dto.LogsResponseDto;
import com.epi.epilog.app.dto.PdfData;
import com.epi.epilog.app.service.fall.PdfService;
import com.epi.epilog.app.service.logs.LogCommandService;
import com.epi.epilog.app.service.logs.LogQueryService;
import com.epi.epilog.global.utils.CustomUserDetails;
import com.epi.epilog.global.utils.DateTimeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
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
@RequiredArgsConstructor
@RequestMapping("/api/logs")
public class LogController {
    private final LogQueryService logQueryService;
    private final LogCommandService logCommandService;
    private final PdfService pdfService;

    /**
     * 월별 일지 개수 조회
     */
    @GetMapping("/count")
    public LogsResponseDto.MonthLogsCount monthLogsCount(@RequestParam(value = "date", required = false) String date){
        LocalDate queryDate;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (date == null) {
             date  = DateTimeConverter.convertLocalDateToString(LocalDate.now());
        }

        queryDate = DateTimeConverter.convertToLocalDate(date);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        LogsResponseDto.MonthLogsCount logsCount = logQueryService.monthLogsCount(queryDate, userDetails.getMember());
        return logsCount;
    }

    /**
     * 일별 일지 목록 조회
     */
    @GetMapping("")
    public LogsResponseDto.DayLogsList dayLogList(@RequestParam(value = "date", required = false) String date){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (date == null){
            date = DateTimeConverter.convertLocalDateToString(LocalDate.now());
        }

        LocalDate queryDate = DateTimeConverter.convertToLocalDate(date);

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return logQueryService.dayLogList(customUserDetails, queryDate);
    }

    /**
     * 일별 평균, 식전후 평균 혈당 조회
     */
    @GetMapping("/bloodsugar/average")
    public LogsResponseDto.DayAvgBloodSugar dayAvgBloodSugar(@RequestParam(value = "date", required = false)String date){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (date == null){
            date = DateTimeConverter.convertLocalDateToString(LocalDate.now());
        }
        LocalDate queryDate = DateTimeConverter.convertToLocalDate(date);
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        return logQueryService.dayAvgBloodSugar(customUserDetails, queryDate);
    }

    /**
     * 일별 혈당 목록 조회
     */
    @GetMapping("/bloodsugar")
    public LogsResponseDto.DayBloodSugarList getDayBloodSugarList(@RequestParam(value = "date", required = false)String date){
        if (date == null) {
            date = DateTimeConverter.convertLocalDateToString(LocalDate.now());
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return logQueryService.dayBloodSugarList(((CustomUserDetails) authentication.getPrincipal()), date);
    }

    /**
     * 월별 체중, 체지방률 목록 조회
     */
    @GetMapping("/weight")
    public LogsResponseDto.MonthWeightList getMonthWeightList(@RequestParam(value="date", required = false)String date){
        if (date == null) {
            date = DateTimeConverter.convertLocalDateToString(LocalDate.now());
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return logQueryService.getMonthWeightList(date, ((CustomUserDetails) authentication.getPrincipal()));
    }

    /**
     * 일지 등록
     */
    @PostMapping("")
    public CommonResponseDto.CommonResponse createLog(@RequestBody LogsRequestDto.LogCreateForm form) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return logCommandService.createLog( ((CustomUserDetails) authentication.getPrincipal()), form);
    }

    /**
     * 일지 상세 조회
     */
    @GetMapping("/{logId}")
    public LogsResponseDto.DetailAllLog detailLog(@PathVariable("logId") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return logQueryService.getDetailLog( ((CustomUserDetails) authentication.getPrincipal()), id);
    }

    /**
     * PDF 변환
     * @param start 시작일
     * @param end 마지막일
     * @return
     * @throws Exception
     */
    @GetMapping("/convert")
    public ResponseEntity<InputStreamResource> createdPDF(@RequestParam(value = "start", required = true)LocalDate start,
                                                          @RequestParam(value = "end", required = true)LocalDate end) throws Exception{

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<PdfData> entries = logQueryService.generatedPdfEntries(((CustomUserDetails) authentication.getPrincipal()), start, end);
        ByteArrayOutputStream baos = pdfService.createPdf("diabetes_log", entries);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=diabetes_log.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(new ByteArrayInputStream(baos.toByteArray())));
    }
}
