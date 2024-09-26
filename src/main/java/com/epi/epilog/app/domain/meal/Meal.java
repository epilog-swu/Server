package com.epi.epilog.app.domain.meal;

import com.epi.epilog.app.domain.enums.MealType;
import com.epi.epilog.app.domain.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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
    @Enumerated(EnumType.STRING)
    private MealType mealType;
    @ColumnDefault("true")
    private Boolean isAlarm;
    @ElementCollection
    @Builder.Default
    @CollectionTable(name="MealTimes", joinColumns = @JoinColumn(name="meal_id"))
    private List<LocalTime> times = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;
}
