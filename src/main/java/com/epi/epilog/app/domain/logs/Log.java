package com.epi.epilog.app.domain.logs;

import com.epi.epilog.app.domain.BaseEntity;
import com.epi.epilog.app.domain.member.Member;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.lang.Nullable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class Log extends BaseEntity {
    @Id
    @Column(name="log_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;
    @NotNull
    private String title; // 제목
    @NotNull
    private LocalDate date; // 발생일자
    @NotNull
    private String occurrenceType; // 발생시간 (식전 / 식후 / 자기 전 / format yyyy-mm-dd 00:00:00)

    // 각 카테고리 별 기록 여부
    @ColumnDefault("false")
    private Boolean isFall;
    @ColumnDefault("false")
    private Boolean isBloodSugar;
    @ColumnDefault("false")
    private Boolean isBloodPressure;
    @ColumnDefault("false")
    private Boolean isWeight;
    @ColumnDefault("false")
    private Boolean isExercise;
    @ColumnDefault("false")
    private Boolean isMood;

    @Nullable
    private Double fallLongitude; // 낙상 위치 기록
    @Nullable
    private Double fallLatitude;
    @Nullable
    private String fallAddress;
    @Nullable
    private String fallAddressImage;

    @Nullable
    private Double bloodSugar; // 혈당

    @Nullable
    private Double systolicBloodPressure; // 수축기 혈압
    @Nullable
    private Double diastolicBloodPressure; // 이완기 혈압
    @Nullable
    private Double heartRate; // 심박수

    @Nullable
    private Double weight; // 몸무게
    @Nullable
    private Double bodyFatPercentage; // 체지방률
    @Nullable
    private String bodyPhoto; // 눈바디 사진

    @OneToMany(mappedBy = "log")
    private List<LogMood> logMood = new ArrayList<>();
}
