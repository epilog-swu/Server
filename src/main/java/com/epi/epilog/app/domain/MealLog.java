package com.epi.epilog.app.domain;

import com.epi.epilog.app.domain.enums.MealStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access= AccessLevel.PRIVATE)
public class MealLog {
    @Id
    @Column(name="meal_log_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="meal_id")
    private Meal meal;
    private LocalDateTime goalTime;
    private LocalDateTime actualTime;
    private Boolean isComplete;
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'상태없음'")
    private MealStatus mealStatus;

    public void updateActualTiime(LocalDateTime time){
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
