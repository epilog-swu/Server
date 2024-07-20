package com.epi.epilog.app.controller;

import com.epi.epilog.app.dto.LogsResponseDto;
import com.epi.epilog.app.service.LogQueryService;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import com.epi.epilog.global.utils.CustomUserDetails;
import com.epi.epilog.global.utils.DateTimeConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/logs")
public class LogController {
    private final LogQueryService logQueryService;

    /**
     * 월별 일지 개수 조회
     */
    @GetMapping("/count")
    public LogsResponseDto.MonthLogsCount monthLogsCount(@RequestParam(value = "date", required = false) String date){
        try {
            LocalDate queryDate;

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
                throw new ApiException(ErrorCode.INVALID_TOKEN);
            }

            if (date == null) {
                 date  = DateTimeConverter.convertLocalDateToString(LocalDate.now());
            }
            queryDate = DateTimeConverter.convertToLocalDate(date);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            LogsResponseDto.MonthLogsCount logsCount = logQueryService.monthLogsCount(queryDate, userDetails.getMember());
            return logsCount;
        } catch (Exception e) {
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, e);
        }
    }

    /**
     * 일별 일지 목록 조회
     */
    @GetMapping("")
    public LogsResponseDto.DayLogsList dayLogList(@RequestParam(value = "date", required = false) String date){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)){
                throw new ApiException(ErrorCode.INVALID_TOKEN);
            }

            if (date == null){
                date = DateTimeConverter.convertLocalDateToString(LocalDate.now());
            }
            LocalDate queryDate = DateTimeConverter.convertToLocalDate(date);

            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            return logQueryService.dayLogList(customUserDetails, queryDate);
        } catch(Exception e) {
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, e);
        }

    }

    /**
     * 일별 평균, 식전후 평균 혈당 조회
     */
    @GetMapping("/bloodsugar/average")
    public LogsResponseDto.DayAvgBloodSugar dayAvgBloodSugar(@RequestParam(value = "date", required = false)String date){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)){
                throw new ApiException(ErrorCode.INVALID_TOKEN);
            }

            if (date == null){
                date = DateTimeConverter.convertLocalDateToString(LocalDate.now());
            }
            LocalDate queryDate = DateTimeConverter.convertToLocalDate(date);
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

            return logQueryService.dayAvgBloodSugar(customUserDetails, queryDate);
        } catch(Exception e){
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, e);
        }
    }

    /**
     * 일별 혈당 목록 조회
     */

    /**
     * 월별 체중, 체지방률 목록 조회
     */

    /**
     * 일지 등록
     */

    /**
     * 일지 수정
     */

    /**
     * 일지 상세 조회
     */

    /**
     * PDF 변환하기
     */

}
