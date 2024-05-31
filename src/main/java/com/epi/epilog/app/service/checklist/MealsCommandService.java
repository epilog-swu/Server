package com.epi.epilog.app.service.checklist;

import com.epi.epilog.app.domain.MealLog;
import com.epi.epilog.app.domain.Member;
import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.dto.CustomUserInfoDto;
import com.epi.epilog.app.dto.MealsResponseDto;
import com.epi.epilog.app.repository.MealLogRepository;
import com.epi.epilog.app.repository.MemberRepository;
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
    private final MemberRepository memberRepository;
    private final MealLogRepository mealLogRepository;

    @Transactional
    public CommonResponseDto.CommonResponse mealsCheck(Long id, MealsResponseDto.ChecklistUpdateDto form) {
        MealLog mealLog = mealLogRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        mealLog.updateActualTiime(DateTimeConverter.convertToLocalDateTime(form.getTime()));
        mealLog.updateStatue(form.getStatus());
        mealLogRepository.save(mealLog);

        return CommonResponseDto.CommonResponse.builder().success(true).message("수정되었습니다.").build();
    }
}
