package com.epi.epilog.app.service.checklist;

import com.epi.epilog.app.domain.enums.MealStatus;
import com.epi.epilog.app.domain.meal.Meal;
import com.epi.epilog.app.domain.meal.MealCheckList;
import com.epi.epilog.app.domain.member.Member;
import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.dto.CommonResponseDto.CommonResponse;
import com.epi.epilog.app.dto.CustomUserInfoDto;
import com.epi.epilog.app.dto.MealsRequestDto.CreateMeal;
import com.epi.epilog.app.dto.MealsResponseDto;
import com.epi.epilog.app.repository.MealCheckListRepository;
import com.epi.epilog.app.repository.MealRepository;
import com.epi.epilog.app.repository.MemberRepository;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import com.epi.epilog.global.utils.DateTimeConverter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MealsCommandService {
    private final MealRepository mealRepository;
    private final MemberRepository memberRepository;
    private final MealCheckListRepository mealCheckListRepository;

    public CommonResponseDto.CommonResponse mealsCheck(Long id, MealsResponseDto.MealChecklistUpdateDto form) {
        MealCheckList mealCheckList = mealCheckListRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        mealCheckList.updateActualTime(DateTimeConverter.convertToLocalDateTime(form.getTime()));
        mealCheckList.updateStatue(form.getStatus());
        mealCheckListRepository.save(mealCheckList);

        return CommonResponseDto.CommonResponse.builder().success(true).message("수정되었습니다.").build();
    }

    public CommonResponse deleteMealTime(CustomUserInfoDto memberInfo, Long timeId) {
        Member member = memberRepository.findById(memberInfo.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Meal meal = mealRepository.findById(timeId)
                .orElseThrow(() -> new ApiException(ErrorCode.MEAL_NOT_FOUND));

        if (meal.getMember().getId() != member.getId()) {
            throw new ApiException(ErrorCode.FORBIDDEN);
        }

        mealRepository.delete(meal);

        return CommonResponse.builder()
                .success(true)
                .message("삭제되었습니다.")
                .build();
    }

    public CommonResponseDto.CommonResponse createMealTime(CustomUserInfoDto memberInfo, List<CreateMeal> forms) {
        Member member = memberRepository.findById(memberInfo.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        List<Meal> mealTimes = forms.stream().map(form ->
                Meal.builder()
                        .time(form.getTime())
                        .mealType(form.getMealType())
                        .isAlarm(form.isAlarm())
                        .member(member)
                        .build()
        ).toList();

        List<Meal> savedMealTimes = mealRepository.saveAll(mealTimes);

        createAutoMealChecklist(savedMealTimes, member);

        return CommonResponseDto.CommonResponse.builder()
                .message("추가되었습니다.")
                .success(true)
                .build();
    }

    public void createAutoMealChecklist(List<Meal> savedMealTimes, Member member) {
        List<Meal> filteredMealTimes = savedMealTimes.stream()
                .filter(Meal::getIsAlarm)
                .toList();
        for (Meal meal : filteredMealTimes) {
            for (int i = 0; i < 7; i++) {
                LocalDate goalDate = LocalDate.now().plusDays(i);
                createMealChecklist(meal, goalDate);
            }
        }
    }

    public void createAutoScheduledMealChecklist() {
        LocalDate targetDate = LocalDate.now().plusDays(8);
        List<Meal> filteredMealTime = getFilteredMealTime();
        if (filteredMealTime.isEmpty()) {
            return;
        }
        for (Meal meal : filteredMealTime) {
            createMealChecklist(meal, targetDate);
        }
    }

    private void createMealChecklist(Meal meal, LocalDate goalDate) {
        LocalDateTime goalTime = LocalDateTime.of(goalDate, meal.getTime());

        String title = meal.getTime().getHour() + "시" +
                (meal.getTime().getMinute() != 0 ? " " + meal.getTime().getMinute() + "분" : "");

        MealCheckList mealChecklist = MealCheckList.builder()
                .actualTime(null)
                .goalTime(goalTime)
                .isComplete(false)
                .mealStatus(MealStatus.상태없음)
                .title(title)
                .meal(meal)
                .build();

        mealCheckListRepository.save(mealChecklist);
    }

    private List<Meal> getFilteredMealTime() {
        List<Meal> allMealTimes = mealRepository.findAll();
        return allMealTimes.stream()
                .filter(Meal::getIsAlarm)
                .toList();
    }
}
