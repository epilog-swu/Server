package com.epi.epilog.app.repository;

import com.epi.epilog.app.domain.log.Log;
import com.epi.epilog.app.domain.log.LogMood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogMoodRepository extends JpaRepository<LogMood, Long> {
    List<LogMood> findByLog(Log log);
}
