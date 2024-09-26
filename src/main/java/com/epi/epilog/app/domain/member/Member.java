package com.epi.epilog.app.domain.member;

import com.epi.epilog.app.domain.BaseEntity;
import com.epi.epilog.app.domain.enums.ActivityLevel;
import com.epi.epilog.app.domain.enums.AgeType;
import com.epi.epilog.app.domain.enums.GenderType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class Member extends BaseEntity {
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    @NotNull
    private String loginId;
    @NotNull
    private String password;
    @NotNull
    private String name;
    private Double stature;
    private Double weight;
    @Enumerated(EnumType.STRING)
    private GenderType gender;
    @Enumerated(EnumType.STRING)
    private AgeType age;
    @Enumerated(EnumType.STRING)
    private ActivityLevel activityLevel;
    @NotNull
    private String protectorName;
    @NotNull
    private String protectorPhone;
    @Column(unique = true)
    private String code;
    @ColumnDefault("false")
    private Boolean linkWatch;
}
