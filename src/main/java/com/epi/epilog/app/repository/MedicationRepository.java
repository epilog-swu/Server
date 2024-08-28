package com.epi.epilog.app.repository;

import com.epi.epilog.app.domain.medication.Medication;
import com.epi.epilog.app.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicationRepository extends JpaRepository<Medication, Long> {
    List<Medication> findAllByMemberOrderByCreatedAt(Member member);
}
