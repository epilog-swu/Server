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
    private final static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("mm:ss");
    private final MealRepository mealRepository;
    private final MemberRepository memberRepository;
    private final MealCheckListRepository mealCheckListRepository;

    public MealsResponseDto.ChecklistDto mealsCheckList(CustomUserInfoDto member, LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H시 mm분");
        DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("H시");

        Member newMember = memberRepository.findById(member.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        List<MealCheckList> mealCheckLists = mealCheckListRepository
                .findAllByMemberAndGoalTime(newMember, date.atStartOfDay(), date.atTime(LocalTime.MAX));

        List<MealsResponseDto.ChecklistStateDto> checklist = new ArrayList<>();

        if (!mealCheckLists.isEmpty()) {
            checklist = mealCheckLists.stream()
                    .map(meal -> MealsResponseDto.ChecklistStateDto.builder()
                            .id(meal.getId())
                            .goalTime(meal.getGoalTime().format(DateTimeConverter.timeFormatter))
                            .title((meal.getGoalTime().getMinute() == 0
                                    ? meal.getGoalTime().format(hourFormatter)
                                    : meal.getGoalTime().format(formatter)) + " " + meal.getMeal().toString())
                            .state(meal.getMealStatus().toString())
                            .isComplete(meal.getIsComplete())
                            .build()
                    ).collect(Collectors.toList());
        }

        return MealsResponseDto.ChecklistDto.builder()
                .date(date)
                .checklist(checklist)
                .build();
    }

    public List<MealTimesDto> mealTimes(CustomUserInfoDto memberInfo) {
        Member member = memberRepository.findById(memberInfo.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        List<Meal> meals = mealRepository.findAllByMember(member);

        return meals.stream().map(meal ->s MealsResponseDto.MealTimesDto.builder()
                .title(meal.getMealType().toString() + " " + meal.getTime().format(TIME_FORMATTER))
                .isAlarm(meal.getIsAlarm())
                .build()).toList();
    }
}
