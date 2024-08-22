package com.epi.epilog.app.controller;

import com.epi.epilog.app.dto.AuthFormDto;
import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public AuthFormDto.SignUpResponseDto signup(@Valid @RequestBody AuthFormDto.SignupFormDto form){
        return authService.signUp(form);
    }

    @PostMapping("/login")
    public ResponseEntity<String> partientLogin(@Valid @RequestBody AuthFormDto.PatientLoginFormDto form){
        String token = authService.patientLogin(form);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login/code")
    public ResponseEntity<String> partientLogin(@Valid @RequestBody AuthFormDto.ProtectorLoginFormDto form){
        String token = authService.protectorLogin(form);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/validation")
    public CommonResponseDto.CommonResponse idValidationCheck(@RequestParam("id") String userId){
        return authService.idValidationCheck(userId);
    }
}
