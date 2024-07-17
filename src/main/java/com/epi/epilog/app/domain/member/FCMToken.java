package com.epi.epilog.app.domain.member;

import jakarta.persistence.*;
import kotlin.Lazy;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access= AccessLevel.PRIVATE)
public class FCMToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="fcm_id")
    private Long id;
    private String token;
    @JoinColumn(name="member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
}
