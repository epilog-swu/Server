package com.epi.epilog.app.service.checklist;

import com.epi.epilog.app.domain.medication.Medication;
import com.epi.epilog.app.domain.medication.MedicationCheckList;
import com.epi.epilog.app.domain.medication.MedicationStatus;
import com.epi.epilog.app.repository.MealCheckListRepository;
import com.epi.epilog.app.repository.MealRepository;
import com.epi.epilog.app.repository.MedicationCheckListRepository;
import com.epi.epilog.app.repository.MedicationRepository;
import com.epi.epilog.global.utils.DateTimeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChecklistSchedulerService {
    private final MealRepository mealRepository;
    private final MealCheckListRepository mealCheckListRepository;
    private final MedicationRepository medicationRepository;
    private final MedicationCheckListRepository medicationCheckListRepository;

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
        List<Medication> all = medicationRepository.findAll();
        if (!all.isEmpty()){
            List<MedicationCheckList> collect = all.stream()
                    .filter(medication->medication.getEndless()==true
                            || medication.getEndDate().isAfter(LocalDate.now().plusDays(8)))
                    .filter(medication -> medication.getWeeks() == null
                            || (medication.getWeeks() != null && medication.getWeeks().contains(LocalDate.now().getDayOfWeek())))
                    .flatMap(medication -> medication.getTimes().stream()
                            .map(times -> MedicationCheckList.builder()
                                    .medication(medication)
                                    .isComplete(false)
                                    .title((times.getMinute() != 0 ?
                                            times.format(DateTimeConverter.krTimeFormatter) :
                                            times.format(DateTimeConverter.krShortTimeFormatter))
                                            + " " + medication.getMedicationName())
                                    .goalTime(LocalDate.now().plusDays(8).atTime(times))
                                    .medicationStatus(MedicationStatus.상태없음)
                                    .build()))
                    .collect(Collectors.toList());
            medicationCheckListRepository.saveAll(collect);
        }
    }
}
