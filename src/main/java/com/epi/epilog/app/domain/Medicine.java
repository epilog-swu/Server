package com.epi.epilog.app.domain;

import com.epi.epilog.app.domain.enums.WeekType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class Medicine extends BaseEntity {
    @Id
    @Column(name="medicine_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    @NotNull
    private Member member;
    @NotNull
    private String name;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @ColumnDefault("true")
    private Boolean isAlarm;
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name="MedicineWeeks", joinColumns = @JoinColumn(name="medicine_id"))
    private List<WeekType> weeks = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name="MedicineTimes", joinColumns = @JoinColumn(name="medicine_id"))
    private List<LocalTime> times = new ArrayList<>();
}
