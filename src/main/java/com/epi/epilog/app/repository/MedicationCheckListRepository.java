package com.epi.epilog.app.repository;

import com.epi.epilog.app.domain.medication.MedicationCheckList;
import com.epi.epilog.app.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MedicationCheckListRepository extends JpaRepository<MedicationCheckList, Long> {
    @Query("select m from MedicationCheckList m where m.medication.member =:member and m.goalTime between :start and :end")
    public List<MedicationCheckList> findAllByMemberAndGoalTime(@Param("member") Member member, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
