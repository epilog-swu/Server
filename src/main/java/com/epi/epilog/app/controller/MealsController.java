package com.epi.epilog.app.controller;

import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.dto.MealsResponseDto;
import com.epi.epilog.app.service.checklist.MealsCommandService;
import com.epi.epilog.app.service.checklist.MealsQueryService;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import com.epi.epilog.global.utils.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meals")
public class MealsController {
    private final MealsQueryService mealsQueryService;
    private final MealsCommandService mealsCommandService;

    @GetMapping("")
    public MealsResponseDto.ChecklistDto mealsChecklist(@RequestParam(value = "date", required = false)LocalDate date){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails){
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return mealsQueryService.mealsCheckList(userDetails.getMember(), date!=null?date:LocalDate.now());
        }
        throw new ApiException(ErrorCode.INVALID_TOKEN);
    }

    @PatchMapping("/{chklstId}")
    public CommonResponseDto.CommonResponse medicineCheck(@PathVariable("chklstId")Long id, @RequestBody @Valid MealsResponseDto.MealChecklistUpdateDto form){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails){
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return mealsCommandService.mealsCheck(id, form);
        }
        throw new ApiException(ErrorCode.INVALID_TOKEN);
    }
}
