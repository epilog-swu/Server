package com.epi.epilog.app.repository;


import com.epi.epilog.app.domain.meal.Meal;
import com.epi.epilog.app.domain.member.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MealRepository extends JpaRepository<Meal, Long> {
    List<Meal> findAllByMember(Member member);
}
