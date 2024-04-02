package com.epi.epilog.app.domain;

import com.epi.epilog.app.domain.enums.GenderType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class Member extends BaseEntity {
    @Id
    @Column(name="member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String loginId;
    private String password;
    private String name;
    private Double stature;
    private Double weight;
    @Enumerated(EnumType.STRING)
    private GenderType gender;
    private String protectorName;
    private String protectorPhone;
    private String code;
    private Boolean linkWatch;
    private Boolean linkProtector;
}
