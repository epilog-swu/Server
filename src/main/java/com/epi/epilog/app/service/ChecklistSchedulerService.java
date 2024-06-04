package com.epi.epilog.app.service;

import com.epi.epilog.app.domain.Meal;
import com.epi.epilog.app.domain.MealLog;
import com.epi.epilog.app.domain.Medicine;
import com.epi.epilog.app.domain.MedicineLog;
import com.epi.epilog.app.domain.enums.MealStatus;
import com.epi.epilog.app.domain.enums.MedicationStatus;
import com.epi.epilog.app.dto.MealsResponseDto;
import com.epi.epilog.app.repository.MealLogRepository;
import com.epi.epilog.app.repository.MealRepository;
import com.epi.epilog.app.repository.MedicineLogRepository;
import com.epi.epilog.app.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChecklistSchedulerService {
    private final MealRepository mealRepository;
    private final MealLogRepository mealLogRepository;
    private final MedicineRepository medicineRepository;
    private final MedicineLogRepository medicineLogRepository;

    /**
     * 식사 체크리스트 스케줄러
     */
    @Scheduled(cron = "0 5 1 * * *")
    @Transactional
    public void mealChecklistScheduler() {
        List<Meal> all = mealRepository.findAll();
        if (!all.isEmpty()){
            List<MealLog> mealChecklist = all.stream().map(meal -> MealLog.builder()
                    .meal(meal)
                    .goalTime(meal.getGoalTime())
                    .isComplete(false)
                    .mealStatus(MealStatus.상태없음)
                    .build()).collect(Collectors.toList());
            mealLogRepository.saveAll(mealChecklist);
            log.info("scheduler) 식사 체크리스트 목록이 성공적으로 등록되었습니다.");
        }
    }

    /**
     * 복약 체크리스트 스케줄러
     */
    @Scheduled(cron = "0 5 1 * * *")
    @Transactional
    public void medicineChecklistScheduler(){
        List<Medicine> all = medicineRepository.findAll();
        if (!all.isEmpty()){
            List<MedicineLog> collect = all.stream()
                    .flatMap(medicine -> medicine.getTimes().stream()
                            .map(times -> MedicineLog.builder()
                                    .medicine(medicine)
                                    .isComplete(false)
                                    .goalTime(times)
                                    .medicationStatus(MedicationStatus.상태없음)
                                    .build()))
                    .collect(Collectors.toList());
            medicineLogRepository.saveAll(collect);
        }
        /
    }
}
