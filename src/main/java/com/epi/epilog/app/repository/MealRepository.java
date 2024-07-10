package com.epi.epilog.app.repository;


import com.epi.epilog.app.domain.meal.Meal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealRepository extends JpaRepository<Meal, Long> {

}
