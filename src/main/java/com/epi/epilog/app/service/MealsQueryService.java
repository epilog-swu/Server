package com.epi.epilog.app.service;

import com.epi.epilog.app.domain.Meal;
import com.epi.epilog.app.domain.MealLog;
import com.epi.epilog.app.domain.Member;
import com.epi.epilog.app.dto.CustomUserInfoDto;
import com.epi.epilog.app.dto.MealsResponseDto;
import com.epi.epilog.app.repository.MealLogRepository;
import com.epi.epilog.app.repository.MealRepository;
import com.epi.epilog.app.repository.MemberRepository;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import com.epi.epilog.global.utils.DateTimeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MealsQueryService {
    private final MemberRepository memberRepository;
    private final MealRepository mealRepository;
    private final MealLogRepository mealLogRepository;

    public MealsResponseDto.ChecklistDto mealsCheckList(CustomUserInfoDto member, LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        Member newMemer = memberRepository.findById(member.getId())
                .orElseThrow(()->new ApiException(ErrorCode.USER_NOT_FOUND));

        List<MealLog> mealLogs = mealLogRepository
                .findAllByMemberAndGoalTime(newMemer, date.atStartOfDay(), date.atTime(LocalTime.MAX));

        List<MealsResponseDto.ChecklistStateDto> checklist = mealLogs.stream()
                .map(meal -> MealsResponseDto.ChecklistStateDto
                        .builder()
                        .goalTime(meal.getGoalTime().format(DateTimeConverter.timeFormatter))
                        .title(meal.getGoalTime().format(formatter) + meal.getMeal().getMealType().toString())
                        .state(meal.getMealStatus().toString())
                        .build()
                ).collect(Collectors.toList());

        return MealsResponseDto.ChecklistDto.builder().date(date)
                .checklist(checklist)
                .build();
    }
}
