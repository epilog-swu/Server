package com.epi.epilog.app.repository;

import com.epi.epilog.app.domain.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
}
