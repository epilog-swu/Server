package com.epi.epilog.app.controller;

import com.epi.epilog.app.domain.member.Member;
import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.repository.MemberRepository;
import com.epi.epilog.app.service.fall.MapService;
import com.epi.epilog.app.service.fall.SMSService;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import com.epi.epilog.global.utils.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/detection")
public class FallDetectionController {
    private final SMSService smsService;
    private final MapService googleMapService;
    private final MemberRepository memberRepository;

    @PostMapping("/emergency")
    public CommonResponseDto.CommonResponse emergencyAlram(
            @RequestBody(required = false) CommonResponseDto.LocationRequest form) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findById(((CustomUserDetails) authentication.getPrincipal())
                .getMember().getId()).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        String mapImage = googleMapService.getMapImageUrl(form.getLatitude(), form.getLongitude());
        String mapImageUrl = googleMapService.createShortURL(mapImage);
        String address = googleMapService.getAddress(form.getLatitude(), form.getLongitude());
        String message = member.getName() + "님 낙상 감지됨" + "\n" + " " + address + " " + mapImageUrl;

        smsService.sendSms("01047367769", member.getProtectorPhone(), message);
        return CommonResponseDto.CommonResponse.builder()
                .success(true)
                .message("전송되었습니다.")
                .build();
    }
}
