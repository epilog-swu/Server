package com.epi.epilog.app.repository;

import com.epi.epilog.app.domain.MealLog;
import com.epi.epilog.app.domain.Member;
import com.epi.epilog.app.dto.CustomUserInfoDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MealLogRepository extends JpaRepository<MealLog, Long> {

    @Query("select m from MealLog m where m.meal.member = :member and m.goalTime between :start and :end")
    List<MealLog> findAllByMemberAndGoalTime(@Param("member") Member member, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
