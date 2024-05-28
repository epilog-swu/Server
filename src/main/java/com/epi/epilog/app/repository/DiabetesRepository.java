package com.epi.epilog.app.repository;

import com.epi.epilog.app.domain.Diabetes;
import com.epi.epilog.app.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DiabetesRepository extends JpaRepository<Diabetes, Long> {
    List<Diabetes> findAllByDateAndMember(LocalDate date, Member member);
}
