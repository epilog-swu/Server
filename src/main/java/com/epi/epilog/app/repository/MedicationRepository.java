package com.epi.epilog.app.repository;

import com.epi.epilog.app.domain.medication.Medication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicationRepository extends JpaRepository<Medication, Long> {
}
