package com.epi.epilog.app.repository;

import com.epi.epilog.app.domain.meal.MealCheckList;
import com.epi.epilog.app.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MealCheckListRepository extends JpaRepository<MealCheckList, Long> {
    @Query("select m from MealCheckList m where m.meal.member = :member and m.goalTime between :start and :end")
    List<MealCheckList> findAllByMemberAndGoalTime(@Param("member") Member member,
                                                   @Param("start") LocalDateTime start,
                                                   @Param("end") LocalDateTime end);
}
