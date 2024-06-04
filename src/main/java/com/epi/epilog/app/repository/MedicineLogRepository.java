package com.epi.epilog.app.repository;

import com.epi.epilog.app.domain.MedicineLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineLogRepository extends JpaRepository<MedicineLog, Long> {
}
