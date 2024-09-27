package com.epi.epilog.app.service.auth;

import com.epi.epilog.app.domain.member.FCMToken;
import com.epi.epilog.app.domain.member.Member;
import com.epi.epilog.app.dto.CustomUserInfoDto;
import com.epi.epilog.app.dto.FCMDto;
import com.epi.epilog.app.repository.FCMRepository;
import com.epi.epilog.app.repository.MemberRepository;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FCMService {
    private final FCMRepository fcmRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Boolean saveFCMToken(FCMDto.FCMRequestForm form, CustomUserInfoDto member) {
        try {
            Member repoMember = memberRepository.findById(member.getId())
                    .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
            FCMToken token = FCMToken.builder()
                    .token(form.getToken())
                    .member(repoMember)
                    .build();
            fcmRepository.save(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
