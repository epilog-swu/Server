package com.epi.epilog.app.repository;

import com.epi.epilog.app.domain.Diabetes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiabetesRepository extends JpaRepository<Diabetes, Long> {
}
