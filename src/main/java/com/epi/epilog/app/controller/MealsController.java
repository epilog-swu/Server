package com.epi.epilog.app.controller;

import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.dto.CommonResponseDto.CommonResponse;
import com.epi.epilog.app.dto.MealsRequestDto;
import com.epi.epilog.app.dto.MealsResponseDto;
import com.epi.epilog.app.dto.MealsResponseDto.MealTimesDto;
import com.epi.epilog.app.service.checklist.MealsCommandService;
import com.epi.epilog.app.service.checklist.MealsQueryService;
import com.epi.epilog.global.utils.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
@Tag(name = "Meal", description = "식사 관리 API")
public class MealsController {
    private final MealsQueryService mealsQueryService;
    private final MealsCommandService mealsCommandService;

    @Operation(summary = "식사 체크리스트 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "식사 체크리스트 조회 성공")
    })
    @GetMapping("")
    public MealsResponseDto.ChecklistDto mealsChecklist(
            @RequestParam(value = "date", required = false) LocalDate date) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return mealsQueryService.mealsCheckList(userDetails.getMember(), date != null ? date : LocalDate.now());
    }

    @Operation(summary = "식사 체크리스트 상태 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "식사 체크리스트 상태 수정 성공")
    })
    @PatchMapping("/{chklstId}")
    public CommonResponseDto.CommonResponse medicineCheck(@PathVariable("chklstId") Long id,
                                                          @RequestBody @Valid MealsResponseDto.MealChecklistUpdateDto form) {
        return mealsCommandService.mealsCheck(id, form);
    }

    @Operation(summary = "식사 시간 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "식사 시간 조회 성공")
    })
    @GetMapping("/time")
    public List<MealTimesDto> mealTimes() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return mealsQueryService.mealTimes(userDetails.getMember());
    }

    @Operation(summary = "식사 시간 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "식사 시간 추가 성공")
    })
    @PostMapping("/time")
    public CommonResponseDto.CommonResponse createMealTime(@RequestBody List<MealsRequestDto.CreateMeal> form) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return mealsCommandService.createMealTime(userDetails.getMember(), form);
    }

    @DeleteMapping("/time/{timeId}")
    public CommonResponseDto.CommonResponse deleteMealTime(@RequestParam Long timeId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return mealsCommandService.deleteMealTime(userDetails.getMember(), timeId);
    }
}
