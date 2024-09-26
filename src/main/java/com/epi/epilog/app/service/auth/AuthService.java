package com.epi.epilog.app.service.auth;

import com.epi.epilog.app.domain.enums.WeekType;
import com.epi.epilog.app.domain.medication.Medication;
import com.epi.epilog.app.domain.member.Member;
import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.dto.CustomUserInfoDto;
import com.epi.epilog.app.dto.AuthFormDto;
import com.epi.epilog.app.repository.MedicationRepository;
import com.epi.epilog.app.repository.MemberRepository;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import com.epi.epilog.global.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final MedicationRepository medicationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ModelMapper modelMapper;
    private static final Random random = new Random();

    public String patientLogin(AuthFormDto.PatientLoginFormDto form) {
        String loginId = form.getLoginId();
        String password = form.getPassword();
        Member member = memberRepository.findByLoginId(loginId);
        if (member == null) {
            throw new ApiException(ErrorCode.LOGIN_FAILED);
        }
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new ApiException(ErrorCode.LOGIN_FAILED);
        }
        CustomUserInfoDto memberInfoDto = modelMapper.map(member, CustomUserInfoDto.class);
        return jwtUtil.createAccessToken(memberInfoDto);
    }

    public String protectorLogin(AuthFormDto.ProtectorLoginFormDto form) {
        String code = form.getCode();
        Member member = memberRepository.findByCode(code);
        if (member == null) {
            throw new ApiException(ErrorCode.LOGIN_FAILED);
        }
        CustomUserInfoDto memberInfoDto = modelMapper.map(member, CustomUserInfoDto.class);
        return jwtUtil.createAccessToken(memberInfoDto);
    }

    public CommonResponseDto.CommonResponse idValidationCheck(String userId) {
        Member member = memberRepository.findByLoginId(userId);
        if (member == null) {
            return CommonResponseDto.CommonResponse.builder()
                    .success(true)
                    .message("사용 가능한 아이디입니다.")
                    .build();
        } else {
            return CommonResponseDto.CommonResponse.builder()
                    .success(false)
                    .message("중복된 아이디입니다.")
                    .build();
        }
    }

    @Transactional
    public AuthFormDto.SignUpResponseDto signUp(AuthFormDto.SignupFormDto form) {
        String code;
        do {
            code = generateCode();
        } while (memberRepository.existsByCode(code));

        Member member = Member.builder()
                .loginId(form.getLoginId())
                .password(passwordEncoder.encode(form.getPassword()))
                .name(form.getName())
                .stature(form.getStature())
                .weight(form.getWeight())
                .gender(form.getGender())
                .protectorName(form.getProtectorName())
                .protectorPhone(form.getProtectorPhone())
                .code(code)
                .linkWatch(false)
                .build();
        Member saveMember = memberRepository.save(member);

        form.getMedication().stream().forEach(medication -> {
                    List<WeekType> weeks = new ArrayList<>();
                    weeks.add(WeekType.월);
                    weeks.add(WeekType.화);
                    weeks.add(WeekType.수);
                    weeks.add(WeekType.목);
                    weeks.add(WeekType.금);
                    weeks.add(WeekType.토);
                    weeks.add(WeekType.일);

                    Medication refMedication = Medication.builder()
                            .medicationName(medication.name)
                            .member(saveMember)
                            .startDate(LocalDate.now())
                            .endless(true)
                            .times(medication.times)
                            .weeks(weeks)
                            .isAlarm(true)
                            .build();
                    medicationRepository.save(refMedication);
                }

        );

        CustomUserInfoDto memberInfoDto = modelMapper.map(saveMember, CustomUserInfoDto.class);
        String token = jwtUtil.createAccessToken(memberInfoDto);

        return AuthFormDto.SignUpResponseDto.builder()
                .success(true)
                .code(code)
                .token(token)
                .build();
    }

    private String generateCode() {
        int number = random.nextInt(900000) + 100000; // 100000부터 999999 사이의 숫자를 생성
        return String.valueOf(number);
    }
}
