package com.epi.epilog.app.service.checklist;

import com.epi.epilog.app.domain.enums.MealStatus;
import com.epi.epilog.app.domain.enums.MedicationStatus;
import com.epi.epilog.app.domain.meal.Meal;
import com.epi.epilog.app.domain.meal.MealCheckList;
import com.epi.epilog.app.domain.medication.Medication;
import com.epi.epilog.app.domain.medication.MedicationCheckList;
import com.epi.epilog.app.domain.member.Member;
import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.dto.CommonResponseDto.CommonResponse;
import com.epi.epilog.app.dto.CustomUserInfoDto;
import com.epi.epilog.app.dto.MealsRequestDto;
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
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MealsCommandService {
    private final MealRepository mealRepository;
    private final MemberRepository memberRepository;
    private final MealCheckListRepository mealCheckListRepository;

    @Transactional
    public CommonResponseDto.CommonResponse mealsCheck(Long id, MealsResponseDto.MealChecklistUpdateDto form) {
        MealCheckList mealCheckList = mealCheckListRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        mealCheckList.updateActualTime(DateTimeConverter.convertToLocalDateTime(form.getTime()));
        mealCheckList.updateStatue(form.getStatus());
        mealCheckListRepository.save(mealCheckList);

        return CommonResponseDto.CommonResponse.builder().success(true).message("수정되었습니다.").build();
    }

    @Transactional
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

        mealRepository.saveAll(mealTimes);

        return CommonResponseDto.CommonResponse.builder()
                .message("추가되었습니다.")
                .success(true)
                .build();
    }

    @Transactional
    public void createAutoMealChecklist() {
        LocalDate targetDate = LocalDate.now().plusDays(8);
        List<Meal> filteredMealTime = getFilteredMealTime();
        if (filteredMealTime.isEmpty()) {
            return;
        }
        for (Meal meal : filteredMealTime) {
            LocalDateTime goalTime = LocalDateTime.of(targetDate, meal.getTime());

            MealCheckList mealChecklist = MealCheckList.builder()
                    .actualTime(null)
                    .goalTime(goalTime)
                    .isComplete(false)
                    .mealStatus(MealStatus.상태없음)
                    .title(meal.getTime().getHour() + "시 " +
                            (meal.getTime().getMinute() != 0 ? meal.getTime().getMinute() + "분" : null))
                    .meal(meal)
                    .build();

            mealCheckListRepository.save(mealChecklist);
        }
    }

    @Nullable
    private List<Meal> getFilteredMealTime() {
        List<Meal> allMealTimes = mealRepository.findAll();
        return allMealTimes.stream()
                .filter(Meal::getIsAlarm)
                .toList();
    }
}
