package com.epi.epilog.app.domain.logs;

import io.micrometer.common.lang.Nullable;
import jakarta.persistence.*;
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
public class LogExercise {
    @Id
    @Column(name="log_exercise_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ColumnDefault("false")
    private boolean detailsState;
    private String type;
    @Nullable
    private String details;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="log_id")
    private Log log;
}
