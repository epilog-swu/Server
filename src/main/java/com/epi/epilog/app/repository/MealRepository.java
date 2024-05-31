package com.epi.epilog.app.repository;


import com.epi.epilog.app.domain.Meal;
import com.epi.epilog.app.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MealRepository extends JpaRepository<Meal, Long> {

}
