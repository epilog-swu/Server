package com.epi.epilog.app.service.checklist;

import com.epi.epilog.app.repository.MealCheckListRepository;
import com.epi.epilog.app.repository.MealRepository;
import com.epi.epilog.app.repository.MedicationCheckListRepository;
import com.epi.epilog.app.repository.MedicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChecklistSchedulerService {
    private final MedicationCommandService medicationCommandService;

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
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    @Transactional
    public void medicineChecklistScheduler(){
        medicationCommandService.createAutoMedicationChecklist(false, null);
    }
}
