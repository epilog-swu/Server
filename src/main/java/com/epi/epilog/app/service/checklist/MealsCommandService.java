package com.epi.epilog.app.service.checklist;

import com.epi.epilog.app.domain.meal.MealCheckList;
import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.dto.MealsResponseDto;
import com.epi.epilog.app.repository.MealCheckListRepository;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import com.epi.epilog.global.utils.DateTimeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MealsCommandService {
    private final MealCheckListRepository mealCheckListRepository;

    /**
     * 체크리스트 수정
     *
     * @param id
     * @param form
     * @return
     */
    @Transactional
    public CommonResponseDto.CommonResponse mealsCheck(Long id, MealsResponseDto.MealChecklistUpdateDto form) {
        MealCheckList mealCheckList = mealCheckListRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        mealCheckList.updateActualTime(DateTimeConverter.convertToLocalDateTime(form.getTime()));
        mealCheckList.updateStatue(form.getStatus());
        mealCheckListRepository.save(mealCheckList);

        return CommonResponseDto.CommonResponse.builder().success(true).message("수정되었습니다.").build();
    }
}
