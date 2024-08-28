package com.epi.epilog.app.domain.meal;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Builder
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Food {
    @Id
    @Column(name="food_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String foodName;
    @ColumnDefault("0")
    private Integer calories;
    @ColumnDefault("0")
    private Double carbohydrates;
    @ColumnDefault("0")
    private Double protein;
    @ColumnDefault("0")
    private Double fat;
    @ColumnDefault("0")
    private Double sugar;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="meal_log_id")
    private MealLog mealLog;
}
