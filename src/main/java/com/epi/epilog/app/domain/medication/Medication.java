package com.epi.epilog.app.domain.medication;

import com.epi.epilog.app.domain.BaseEntity;
import com.epi.epilog.app.domain.enums.WeekType;
import com.epi.epilog.app.domain.member.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class Medication extends BaseEntity {
    @Id
    @Column(name="medication_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    @NotNull
    private Member member;
    @NotNull
    private String medicationName;
    private LocalDate startDate;
    @Nullable
    private LocalDate endDate;
    @NotNull
    private Boolean endless;
    @NotNull
    private Boolean isAlarm;
    @Nullable
    private String precautions; // 주의사항
    @Nullable
    private String storageMethod; // 보관방법
    @Nullable
    private String effectiveness; // 효능
    @ElementCollection
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @CollectionTable(name="MedicationWeeks", joinColumns = @JoinColumn(name="medication_id"))
    @Nullable
    private List<WeekType> weeks = new ArrayList<>();
    @ElementCollection
    @Builder.Default
    @CollectionTable(name="MedicationTimes", joinColumns = @JoinColumn(name="medication_id"))
    @Nullable
    private List<LocalTime> times = new ArrayList<>();
}
