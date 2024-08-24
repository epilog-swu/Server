package com.epi.epilog.app.controller;

import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.dto.FCMDto;
import com.epi.epilog.app.service.auth.FCMService;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import com.epi.epilog.global.utils.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fcm")
public class FCMController {
    private final FCMService fcmService;

    /**
     * token 저장
     * @param form
     * @return
     */
    @PostMapping("/token")
    public CommonResponseDto.CommonResponse saveFCMToken(@RequestBody FCMDto.FCMRequestForm form) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Boolean success = fcmService.saveFCMToken(form, userDetails.getMember());

            return CommonResponseDto.CommonResponse.builder()
                    .success(success==false?false:true)
                    .message(success==false?"토큰 저장에 실패했습니다.":"토큰 저장에 성공했습니다.")
                    .build();

        } catch (Exception e) {
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, e);
        }
    }
}
