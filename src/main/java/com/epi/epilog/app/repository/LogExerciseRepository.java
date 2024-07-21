package com.epi.epilog.app.repository;

import com.epi.epilog.app.domain.log.Log;
import com.epi.epilog.app.domain.log.LogExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;

public interface LogExerciseRepository extends JpaRepository<LogExercise, Long> {
    List<LogExercise> findByLog(Log log);
}
