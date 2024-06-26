package com.epi.epilog.app.domain;

import com.epi.epilog.app.domain.enums.MealType;
import com.epi.epilog.app.domain.enums.WeekType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@NoArgsConstructor(access=PROTECTED)
@AllArgsConstructor(access=PRIVATE)
public class Meal {
    @Id
    @Column(name="meal_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private LocalTime goalTime;
    @Enumerated(EnumType.STRING)
    private MealType mealType;
    @ElementCollection
    @Builder.Default
    @CollectionTable(name="MealWeeks", joinColumns =  @JoinColumn(name="meal_id"))
    @Enumerated(EnumType.STRING)
    private List<WeekType> weeks = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;
    @ColumnDefault("true")
    private Boolean isAlarm;
}
