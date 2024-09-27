package com.epi.epilog.app.controller;

import com.epi.epilog.app.dto.AuthFormDto;
import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /**
     * 회원가입
     * @param form
     * @return
     */
    @PostMapping("/signup")
    public AuthFormDto.SignUpResponseDto signup(@Valid @RequestBody AuthFormDto.SignupFormDto form){
        return authService.signUp(form);
    }

    /**
     * 모바일 로그인
     * @param form
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<String> partientLogin(@Valid @RequestBody AuthFormDto.PatientLoginFormDto form){
        String token = authService.patientLogin(form);
        return ResponseEntity.ok(token);
    }

    /**
     * 워치 로그인
     * @param form
     * @return
     */
    @PostMapping("/login/code")
    public ResponseEntity<String> partientLogin(@Valid @RequestBody AuthFormDto.ProtectorLoginFormDto form){
        String token = authService.protectorLogin(form);
        return ResponseEntity.ok(token);
    }

    /**
     * 닉네임 중복 검사
     * @param userId
     * @return
     */
    @GetMapping("/validation")
    public CommonResponseDto.CommonResponse idValidationCheck(@RequestParam("id") String userId){
        return authService.idValidationCheck(userId);
    }
}
