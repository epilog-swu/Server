package com.epi.epilog.app.controller;

import com.epi.epilog.app.domain.Member;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import com.epi.epilog.global.utils.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Security;

@RestController
@Slf4j
public class TestController {
    @GetMapping("/test")
    public ResponseEntity<String> testAPI(){
        return ResponseEntity.ok("successful");
    }
    @GetMapping("/auth/test")
    public ResponseEntity<String> testAuthAPI(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("soobin", authentication.toString());
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return ResponseEntity.ok("successful");
        }
        throw new ApiException(ErrorCode.INVALID_TOKEN);
    }
}
