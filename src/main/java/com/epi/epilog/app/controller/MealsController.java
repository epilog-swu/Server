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

    /**
     * 식사 체크리스트 조회
     * @param date
     * @return
     */
    @GetMapping("")
    public MealsResponseDto.ChecklistDto mealsChecklist(@RequestParam(value = "date", required = false)LocalDate date){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return mealsQueryService.mealsCheckList(userDetails.getMember(), date!=null?date:LocalDate.now());
    }

    /**
     * 식사 체크리스트 상태 수정
     * 유저 검증하는 절차 추가
     * @param id
     * @param form
     * @return
     */
    @PatchMapping("/{chklstId}")
    public CommonResponseDto.CommonResponse medicineCheck(@PathVariable("chklstId")Long id, @RequestBody @Valid MealsResponseDto.MealChecklistUpdateDto form){
        return mealsCommandService.mealsCheck(id, form);
    }
}
