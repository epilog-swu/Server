package com.epi.epilog.app.repository;

import com.epi.epilog.app.domain.logs.Log;
import com.epi.epilog.app.domain.logs.LogExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogExerciseRepository extends JpaRepository<LogExercise, Long> {
    List<LogExercise> findByLog(Log log);
}
