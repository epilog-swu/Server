package com.epi.epilog.app.repository;

import com.epi.epilog.app.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    public Member findByLoginId(String loginId);
    public Member findByCode(String code);
}
