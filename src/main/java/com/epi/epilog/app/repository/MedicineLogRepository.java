package com.epi.epilog.app.repository;

import com.epi.epilog.app.domain.Medicine;
import com.epi.epilog.app.domain.MedicineLog;
import com.epi.epilog.app.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MedicineLogRepository extends JpaRepository<MedicineLog, Long> {
    @Query("select m from MedicineLog m where m.medicine.member =:member and m.goalTime between :start and :end")
    public List<MedicineLog> findAllByMemberAndGoalTime(@Param("member") Member member, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
