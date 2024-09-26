package com.epi.epilog.app.repository;

import com.epi.epilog.app.domain.logs.Log;
import com.epi.epilog.app.domain.logs.LogMood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogMoodRepository extends JpaRepository<LogMood, Long> {
}
