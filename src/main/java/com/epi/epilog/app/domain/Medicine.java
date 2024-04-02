package com.epi.epilog.app.domain;

import com.epi.epilog.app.domain.enums.WeekType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class Medicine {
    @Id
    @Column(name="medicine_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;
    private String name;
    @ElementCollection
    @CollectionTable(name="MedicineDoses", joinColumns = @JoinColumn(name="medicine_id"))
    private List<LocalDateTime> doses = new ArrayList<>();
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isAlarm;
    @ElementCollection
    @CollectionTable(name="MedicineWeeks", joinColumns = @JoinColumn(name="seizure_id"))
    private List<WeekType> weseks = new ArrayList<>();
}
