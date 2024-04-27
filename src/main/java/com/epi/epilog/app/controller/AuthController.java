package com.epi.epilog.app.controller;

import com.epi.epilog.app.dto.LoginFormDto;
import com.epi.epilog.app.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("login/patient")
    public ResponseEntity<String> partientLogin(@Valid @RequestBody LoginFormDto.PatientLoginFormDto form){
        String token = authService.patientLogin(form);
        return ResponseEntity.ok(token);
    }

    @PostMapping("login/protector")
    public ResponseEntity<String> partientLogin(@Valid @RequestBody LoginFormDto.ProtectorLoginFormDto form){
        String token = authService.protectorLogin(form);
        return ResponseEntity.ok(token);
    }
}
