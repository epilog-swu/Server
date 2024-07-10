package com.epi.epilog.app.domain.diabets;

import com.epi.epilog.app.domain.BaseEntity;
import com.epi.epilog.app.domain.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.time.LocalDate;

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
    private String title; // 제목
    private LocalDate date; // 발생일자
    private String occurrenceType; // 발생시간 (식전 / 식후 / 자기 전 / format 00:00)
    @Nullable
    private Double bloodSugar; // 혈당
    @Nullable
    private Double systolicBloodPressure; // 수축기 혈압
    @Nullable
    private Double diastolicBloodPressure; // 이완기 혈압
    @Nullable
    private Integer heartRate; // 심박수
    @Nullable
    private Double weight; // 몸무게
    @Nullable
    private Double bodyFatPercentage; // 체지방률
    @Nullable
    private String bodyPhoto; // 눈바디 사진
}
