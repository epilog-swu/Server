package com.epi.epilog.app.domain;

import com.epi.epilog.app.domain.enums.OccurrenceType;
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
public class Seizure extends BaseEntity {
    @Id
    @Column(name="seizure_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;
    private LocalDateTime occurrenceDate;
    @Enumerated(EnumType.STRING)
    private OccurrenceType occurrenceType;
    private String title;
    private String seizureType;
    @ElementCollection
    @CollectionTable(name="SeizureDuring", joinColumns = @JoinColumn(name="seizure_id"))
    private List<String> seizureDuring = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name="SeizureAfter", joinColumns = @JoinColumn(name="seizure_id"))
    private List<String> afterSeizure = new ArrayList<>();
    private String duration;
    @ElementCollection
    @CollectionTable(name="SeizureMood", joinColumns = @JoinColumn(name="seizure_id"))
    private List<String> mood = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name="SeizureExpectedFactors", joinColumns = @JoinColumn(name="seizure_id"))
    private List<String> expectedFactors = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name="SeizureActivity", joinColumns = @JoinColumn(name="seizure_id"))
    private List<String> activity = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name="SeizureMedication", joinColumns = @JoinColumn(name="seizure_id"))
    private List<String> medication = new ArrayList<>();
    private String comment;

}
