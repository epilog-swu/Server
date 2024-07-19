package com.epi.epilog.app.repository;

import com.epi.epilog.app.domain.log.Log;
import com.epi.epilog.app.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LogRepository extends JpaRepository<Log, Long> {
    List<Log> findAllByDateAndMember(LocalDate date, Member member);

    @Query("select l from Log l where l.member = :member and function('YEAR', l.date) = :year and function('MONTH', l.date) = :month ")
    List<Log> findAllByMonthAndMember(@Param("year") Integer year, @Param("month")Integer month, @Param("member")Member member);
}
