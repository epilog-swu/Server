package com.epi.epilog.app.controller;

import com.epi.epilog.app.domain.Member;
import com.epi.epilog.app.dto.AccelerometerData;
import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.repository.MemberRepository;
import com.epi.epilog.app.service.FallDetectionService;
import com.epi.epilog.app.service.MapService;
import com.epi.epilog.app.service.SMSService;
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

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/detection")
public class FallDetectionController {
    private final FallDetectionService fallDetectionService;
    private final SMSService smsService;
    private final MapService googleMapService;
    private final MemberRepository memberRepository;

    @PostMapping("/fall")
    public boolean detectFall(@RequestBody List<AccelerometerData> data) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails){
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return fallDetectionService.isFallDetected(data);
        }
        throw new ApiException(ErrorCode.INVALID_TOKEN);
    }

    @PostMapping("/emergency")
    public CommonResponseDto.CommonResponse emergencyAlram(
            @RequestBody(required = false) CommonResponseDto.LocationRequest form) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails){
            Member member = memberRepository.findById(((CustomUserDetails) authentication.getPrincipal())
                    .getMember().getId()).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
            try {
                String mapImage = googleMapService.getMapImageUrl(form.getLatitude(), form.getLongitude());
                log.info("map = "+mapImage);
                String mapImageUrl = googleMapService.createShortURL(mapImage);
                log.info("map real url = "+mapImageUrl);
                String address = googleMapService.getAddress(form.getLatitude(), form.getLongitude());
                log.info("address = "+address);
                String message = member.getName() + "님 낙상 감지됨" + "\n" + " " + address + " " + mapImageUrl;

                log.info("message = "+message);

                smsService.sendSms(member.getProtectorPhone(), "01086907017", message);
                return CommonResponseDto.CommonResponse.builder()
                        .success(true)
                        .message("전송되었습니다.")
                        .build();
            } catch (Exception e) {
                throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        throw new ApiException(ErrorCode.INVALID_TOKEN);
    }
}
