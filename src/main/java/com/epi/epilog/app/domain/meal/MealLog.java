package com.epi.epilog.app.domain.meal;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.lang.Nullable;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MealLog {
    @Id
    @Column(name="meal_log_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Nullable
    private String mealImage;
    private String memo;
    @ColumnDefault("0")
    private Integer totalCalories;
    @ColumnDefault("0")
    private Double totalCarbohydrates;
    @ColumnDefault("0")
    private Double totalProtein;
    @ColumnDefault("0")
    private Double totalPat;
    @ColumnDefault("0")
    private Double totalSugar;
}
