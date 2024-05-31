package com.epi.epilog.app.service.checklist;

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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MealsQueryService {
    private final MemberRepository memberRepository;
    private final MealLogRepository mealLogRepository;

    public MealsResponseDto.ChecklistDto mealsCheckList(CustomUserInfoDto member, LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H시 mm분");
        DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("H시");

        Member newMember = memberRepository.findById(member.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        List<MealLog> mealLogs = mealLogRepository
                .findAllByMemberAndGoalTime(newMember, date.atStartOfDay(), date.atTime(LocalTime.MAX));

        List<MealsResponseDto.ChecklistStateDto> checklist = new ArrayList<>();

        if (!mealLogs.isEmpty()){
            checklist = mealLogs.stream()
                    .map(meal -> MealsResponseDto.ChecklistStateDto.builder()
                            .id(meal.getId())
                            .goalTime(meal.getGoalTime().format(DateTimeConverter.timeFormatter))
                            .title((meal.getGoalTime().getMinute()==0?meal.getGoalTime().format(hourFormatter):meal.getGoalTime().format(formatter)) + " " + meal.getMeal().getMealType().toString())
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
}
