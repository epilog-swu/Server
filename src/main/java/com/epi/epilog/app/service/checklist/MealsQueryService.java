package com.epi.epilog.app.service.checklist;

import com.epi.epilog.app.domain.meal.Meal;
import com.epi.epilog.app.domain.meal.MealCheckList;
import com.epi.epilog.app.domain.member.Member;
import com.epi.epilog.app.dto.CustomUserInfoDto;
import com.epi.epilog.app.dto.MealsResponseDto;
import com.epi.epilog.app.dto.MealsResponseDto.MealTimesDto;
import com.epi.epilog.app.repository.MealCheckListRepository;
import com.epi.epilog.app.repository.MealRepository;
import com.epi.epilog.app.repository.MemberRepository;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import com.epi.epilog.global.utils.DateTimeConverter;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MealsQueryService {
    private final static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private final MealRepository mealRepository;
    private final MemberRepository memberRepository;
    private final MealCheckListRepository mealCheckListRepository;

    public MealsResponseDto.ChecklistDto mealsCheckList(CustomUserInfoDto member, LocalDate date) {
        Member newMember = memberRepository.findById(member.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        List<MealCheckList> mealCheckLists = mealCheckListRepository.findAllByMemberAndGoalTime(
                newMember, date.atStartOfDay(), date.atTime(LocalTime.MAX)
        );
        if (mealCheckLists.isEmpty()) {
            return null;
        }
        List<MealsResponseDto.ChecklistStateDto> checklist = mealCheckLists.stream()
                .map(this::convertToChecklistStateDto)
                .collect(Collectors.toList());
        return MealsResponseDto.ChecklistDto.builder()
                .date(date)
                .checklist(checklist)
                .build();
    }

    public List<MealTimesDto> mealTimes(CustomUserInfoDto memberInfo) {
        Member member = memberRepository.findById(memberInfo.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        List<Meal> meals = mealRepository.findAllByMember(member);

        return meals.stream().map(meal -> MealsResponseDto.MealTimesDto.builder()
                        .id(meal.getId())
                        .title(meal.getMealType().toString() + " " + meal.getTime().format(TIME_FORMATTER))
                        .isAlarm(meal.getIsAlarm().booleanValue())
                        .build())
                .toList();
    }

    private MealsResponseDto.ChecklistStateDto convertToChecklistStateDto(MealCheckList meal) {
        String formattedGoalTime = formatGoalTime(meal.getGoalTime());
        String title = formattedGoalTime + " " + meal.getMeal().getMealType().toString();

        return MealsResponseDto.ChecklistStateDto.builder()
                .id(meal.getId())
                .goalTime(formattedGoalTime)
                .title(title)
                .time(formattedGoalTime)
                .mealType(meal.getMeal().getMealType().toString())
                .state(meal.getMealStatus().toString())
                .isComplete(meal.getIsComplete())
                .build();
    }

    private String formatGoalTime(LocalDateTime goalTime) {
        DateTimeFormatter fullFormatter = DateTimeFormatter.ofPattern("H시 mm분");
        DateTimeFormatter hourOnlyFormatter = DateTimeFormatter.ofPattern("H시");

        return (goalTime.getMinute() == 0)
                ? goalTime.format(hourOnlyFormatter)
                : goalTime.format(fullFormatter);
    }
}
