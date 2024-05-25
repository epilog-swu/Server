package com.epi.epilog.app.controller;

import com.epi.epilog.app.domain.Member;
import com.epi.epilog.app.dto.AccelerometerData;
import com.epi.epilog.app.service.FallDetectionService;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import com.epi.epilog.global.utils.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/detection")
public class FallDetectionController {
    private final FallDetectionService fallDetectionService;
    @PostMapping("/fall")
    public boolean detectFall(@RequestBody List<AccelerometerData> data) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails){
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return fallDetectionService.isFallDetected(data);
        }
        throw new ApiException(ErrorCode.INVALID_TOKEN);
    }
}
