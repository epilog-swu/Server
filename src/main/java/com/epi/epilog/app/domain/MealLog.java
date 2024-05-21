package com.epi.epilog.app.domain;

import com.epi.epilog.app.domain.enums.MealStatus;
import com.epi.epilog.app.domain.enums.MedicationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access= AccessLevel.PRIVATE)
public class MealLog {
    @Id
    @Column(name="meal_log_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="medicine_id")
    private Medicine medicine;
    private LocalDateTime goalTime;
    private LocalDateTime actualTime;
    private Boolean isComplete;
    @Builder.Default
    private MealStatus mealStatus = MealStatus.상태없음;
}
