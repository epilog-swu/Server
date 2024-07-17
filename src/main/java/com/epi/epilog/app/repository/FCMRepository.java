package com.epi.epilog.app.repository;

import com.epi.epilog.app.domain.member.FCMToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FCMRepository extends JpaRepository<FCMToken, Long> {
}
