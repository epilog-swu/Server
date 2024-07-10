package com.epi.epilog.app.service;

import com.epi.epilog.app.domain.meal.Meal;
import com.epi.epilog.app.domain.meal.MealCheckList;
import com.epi.epilog.app.domain.medication.Medication;
import com.epi.epilog.app.domain.medication.MedicationCheckList;
import com.epi.epilog.app.domain.meal.MealStatus;
import com.epi.epilog.app.domain.medication.MedicationStatus;
import com.epi.epilog.app.repository.MealLogRepository;
import com.epi.epilog.app.repository.MealRepository;
import com.epi.epilog.app.repository.MedicineLogRepository;
import com.epi.epilog.app.repository.MedicineRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
//    @Scheduled(cron = "0 0 15 * * ?")
//    @Transactional
//    public void mealChecklistScheduler() {
//        List<Meal> all = mealRepository.findAll();
//        if (!all.isEmpty()){
//            List<MealCheckList> mealChecklist = all.stream().map(meal -> MealCheckList.builder()
//                    .meal(meal)
//                    .goalTime(LocalDate.now().plusDays(1).atTime(meal.getGoalTime()))
//                    .isComplete(false)
//                    .mealStatus(MealStatus.상태없음)
//                    .build()).collect(Collectors.toList());
//            mealLogRepository.saveAll(mealChecklist);
//            log.info("scheduler) 식사 체크리스트 목록이 성공적으로 등록되었습니다.");
//        }
//    }

    /**
     * 복약 체크리스트 스케줄러
     */
    @Scheduled(cron = "0 0 15 * * ?")
    @Transactional
    public void medicineChecklistScheduler(){
        List<Medication> all = medicineRepository.findAll();
        if (!all.isEmpty()){
            List<MedicationCheckList> collect = all.stream()
                    .flatMap(medication -> medication.getTimes().stream()
                            .map(times -> MedicationCheckList.builder()
                                    .medication(medication)
                                    .isComplete(false)
                                    .goalTime(LocalDate.now().plusDays(1).atTime(times))
                                    .medicationStatus(MedicationStatus.상태없음)
                                    .build()))
                    .collect(Collectors.toList());
            medicineLogRepository.saveAll(collect);
        }
    }
}
