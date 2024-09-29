package com.epi.epilog.app.controller;

import com.epi.epilog.app.dto.AuthFormDto;
import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.service.auth.AuthService;
import com.google.api.Http;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "계정 관련 API")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "회원가입", description = "모바일 회원가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
    })
    @PostMapping("/signup")
    public ResponseEntity<AuthFormDto.SignUpResponseDto> signup(@Valid @RequestBody AuthFormDto.SignupFormDto form) {
        AuthFormDto.SignUpResponseDto signUpResponseDto = authService.signUp(form);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(signUpResponseDto);
    }

    @Operation(summary = "모바일 로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모바일 로그인 성공"),
            @ApiResponse(responseCode = "401", description = "모바일 로그인 실패 - 인증 실패", content = @Content(schema = @Schema(implementation = com.epi.epilog.global.exception.ErrorResponse.class))),
    })
    @PostMapping("/login")
    public ResponseEntity<String> partientLogin(@Valid @RequestBody AuthFormDto.PatientLoginFormDto form) {
        String token = authService.patientLogin(form);
        return ResponseEntity.ok(token);
    }

    @Operation(summary = "워치 로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "워치 로그인 성공"),
            @ApiResponse(responseCode = "401", description = "워치 로그인 실패 - 인증 실패", content = @Content(schema = @Schema(implementation = com.epi.epilog.global.exception.ErrorResponse.class)))
    })
    @PostMapping("/login/code")
    public ResponseEntity<String> partientLogin(@Valid @RequestBody AuthFormDto.ProtectorLoginFormDto form) {
        String token = authService.protectorLogin(form);
        return ResponseEntity.ok(token);
    }

    @Operation(summary = "닉네임 중복 검사")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임 중복 검사 성공"),
    })
    @GetMapping("/validation")
    public CommonResponseDto.CommonResponse idValidationCheck(@RequestParam("id") String userId) {
        return authService.idValidationCheck(userId);
    }
}
