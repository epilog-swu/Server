package com.epi.epilog.app.service.checklist;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.epi.epilog.app.domain.enums.MealStatus;
import com.epi.epilog.app.domain.enums.MealType;
import com.epi.epilog.app.domain.meal.Meal;
import com.epi.epilog.app.domain.meal.MealCheckList;
import com.epi.epilog.app.domain.member.Member;
import com.epi.epilog.app.dto.CustomUserInfoDto;
import com.epi.epilog.app.dto.MealsResponseDto;
import com.epi.epilog.app.repository.MealCheckListRepository;
import com.epi.epilog.app.repository.MealRepository;
import com.epi.epilog.app.repository.MemberRepository;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MealsQueryServiceTest {
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MealCheckListRepository mealCheckListRepository;

    @InjectMocks
    private MealsQueryService mealsQueryService;

    private Long memberId;
    private Member member;
    private CustomUserInfoDto customUserInfoDto;
    private LocalDate date;

    @BeforeEach
    void setUp() {
        memberId = 1L;
        date = LocalDate.of(2024, 11, 27);
        member = createMember(memberId, "user123");
        customUserInfoDto = createCustomUserInfoDto(memberId, "user123", "1234", "user", "123456");
    }

    private Member createMember(Long id, String loginId) {
        return Member.builder()
                .id(id)
                .loginId(loginId)
                .build();
    }

    private CustomUserInfoDto createCustomUserInfoDto(Long id, String loginId, String password, String name,
                                                      String phoneNumber) {
        return new CustomUserInfoDto(id, loginId, password, name, phoneNumber);
    }

    @Test
    @DisplayName("식사 체크리스트 성공적으로 반환")
    void mealsCheckList_success() {
        // Given
        Meal meal = Meal.builder()
                .id(10L)
                .mealType(MealType.아침식사)
                .build();

        MealCheckList mealCheckList = MealCheckList.builder()
                .id(100L)
                .goalTime(date.atTime(8, 0))
                .meal(meal)
                .mealStatus(MealStatus.상태없음)
                .isComplete(false)
                .build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(mealCheckListRepository.findAllByMemberAndGoalTime(
                eq(member), eq(date.atStartOfDay()), eq(date.atTime(LocalTime.MAX))))
                .thenReturn(List.of(mealCheckList));

        // When
        MealsResponseDto.ChecklistDto result = mealsQueryService.mealsCheckList(customUserInfoDto, date);

        // Then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getDate()).isEqualTo(date);
        Assertions.assertThat(result.getChecklist()).hasSize(1);

        MealsResponseDto.ChecklistStateDto checklistItem = result.getChecklist().get(0);
        Assertions.assertThat(checklistItem.getId()).isEqualTo(100L);
        String expectedGoalTime = date.atTime(8, 0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Assertions.assertThat(checklistItem.getGoalTime()).isEqualTo(expectedGoalTime);
        Assertions.assertThat(checklistItem.getTitle()).isEqualTo("8시 아침식사");
        Assertions.assertThat(checklistItem.getMealType()).isEqualTo("아침식사");
        Assertions.assertThat(checklistItem.getState()).isEqualTo("상태없음");
        Assertions.assertThat(checklistItem.isComplete()).isFalse();
    }

    @Test
    @DisplayName("사용자가 존재하지 않을 때 예외 발생")
    void mealsCheckList_userNotFound() {
        // Given
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        // When & Then
        Assertions.assertThatThrownBy(() -> mealsQueryService.mealsCheckList(customUserInfoDto, date))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("식사 체크리스트가 비어 있을 때 null 반환")
    void mealsCheckList_emptyChecklist() {
        // Given
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(mealCheckListRepository.findAllByMemberAndGoalTime(
                eq(member), eq(date.atStartOfDay()), eq(date.atTime(LocalTime.MAX))))
                .thenReturn(List.of());

        // When
        MealsResponseDto.ChecklistDto result = mealsQueryService.mealsCheckList(customUserInfoDto, date);

        // Then
        Assertions.assertThat(result).isNull();
    }
}
