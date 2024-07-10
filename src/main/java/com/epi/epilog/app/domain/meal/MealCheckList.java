package com.epi.epilog.app.domain.meal;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access= AccessLevel.PRIVATE)
public class MealCheckList {
    @Id
    @Column(name="meal_checklist_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="meal_id")
    private Meal meal;
    private String title;
    private LocalDateTime goalTime;
    private LocalDateTime actualTime;
    private Boolean isComplete;
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'상태없음'")
    private MealStatus mealStatus;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="meal_log_id")
    private MealLog mealLog;

    public void updateActualTime(LocalDateTime time){
        this.actualTime = time;
    }

    public void updateStatue(MealStatus mealStatus){
        this.mealStatus = mealStatus;
        if (mealStatus == MealStatus.상태없음){
            this.isComplete = false;
        } else {
            this.isComplete = true;
        }
    }
}
