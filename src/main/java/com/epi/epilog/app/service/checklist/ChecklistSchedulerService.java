package com.epi.epilog.app.service.checklist;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChecklistSchedulerService {
    private final MedicationCommandService medicationCommandService;
    private final MealsCommandService mealsCommandService;

    /**
     * TODO: 식사 체크리스트 스케줄러
     */
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    @Transactional
    public void mealChecklistScheduler() {
        mealsCommandService.createAutoScheduledMealChecklist();
    }

    /**
     * 복약 체크리스트 스케줄러
     */
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Seoul")
    @Transactional
    public void medicineChecklistScheduler() {
        medicationCommandService.createAutoMedicationChecklist(false, null);
    }
}
