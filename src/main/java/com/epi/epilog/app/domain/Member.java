package com.epi.epilog.app.domain;

import com.epi.epilog.app.domain.enums.GenderType;
import com.epi.epilog.app.domain.enums.RoleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.validator.constraints.UniqueElements;

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
    @NotNull
    private String protectorName;
    @NotNull
    private String protectorPhone;
    @Column(unique = true)
    private String code;
    @ColumnDefault("false")
    private Boolean linkWatch;
}
