package com.epi.epilog.app.domain.meal;

import com.epi.epilog.app.domain.enums.MealStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MealCheckList {
    @Id
    @Column(name = "meal_checklist_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private LocalDateTime goalTime;
    private LocalDateTime actualTime;
    private Boolean isComplete;
    @Enumerated(EnumType.STRING)
    @ColumnDefault("상태없음")
    private MealStatus mealStatus;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id")
    private Meal meal;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_log_id")
    private MealLog mealLog;

    public void updateActualTime(LocalDateTime time) {
        this.actualTime = time;
    }

    public void updateStatue(MealStatus mealStatus) {
        this.mealStatus = mealStatus;
        if (mealStatus == MealStatus.상태없음) {
            this.isComplete = false;
        } else {
            this.isComplete = true;
        }
    }
}
