package com.epi.epilog.app.controller;

import com.epi.epilog.app.dto.MealsResponseDto;
import com.epi.epilog.app.service.MealsQueryService;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import com.epi.epilog.global.utils.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meals")
public class MealsController {
    private final MealsQueryService mealsQueryService;

    @GetMapping("")
    public MealsResponseDto.ChecklistDto mealsChecklist(@RequestParam(value = "date", required = false)LocalDate date){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails){
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return mealsQueryService.mealsCheckList(userDetails.getMember(), date!=null?date:LocalDate.now());
        }
        throw new ApiException(ErrorCode.INVALID_TOKEN);
    }

}
