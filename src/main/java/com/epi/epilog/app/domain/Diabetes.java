package com.epi.epilog.app.domain;

import com.epi.epilog.app.domain.annotations.ValidOccurenceType;
import com.epi.epilog.app.domain.enums.OccurrenceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
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
public class Diabetes extends BaseEntity {
    @Id
    @Column(name="diabetes_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;
    // 발생일자
    private LocalDate date;

    // 발생시간 (식전 / 식후 / 자기 전 / format 00:00)
    private String occurrenceType;
    private String title;
    @ColumnDefault("0")
    private Integer bloodSugar;
    // 기분
    @ElementCollection
    @CollectionTable(name="DiabetesMood", joinColumns = @JoinColumn(name="diabetes_id"))
    private List<String> mood = new ArrayList<>();
    // 신체활동
    @ElementCollection
    @CollectionTable(name="DiabetesActivity", joinColumns = @JoinColumn(name="diabetes_id"))
    private List<String> activity = new ArrayList<>();
    // 복용 약
    @ElementCollection
    @CollectionTable(name="DiabetesMedication", joinColumns = @JoinColumn(name="diabetes_id"))
    private List<String> medication = new ArrayList<>();
    private String comment;
}
