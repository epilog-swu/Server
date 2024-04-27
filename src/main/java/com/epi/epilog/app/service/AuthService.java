package com.epi.epilog.app.service;

import com.epi.epilog.app.domain.Member;
import com.epi.epilog.app.dto.CustomUserInfoDto;
import com.epi.epilog.app.dto.LoginFormDto;
import com.epi.epilog.app.repository.MemberRepository;
import com.epi.epilog.global.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ModelMapper modelMapper;

    public String partientLogin(LoginFormDto.PatientLoginFormDto form) {
        String loginId = form.getLoginId();
        String password = form.getPassword();
        Member member = memberRepository.findByLoginId(loginId);
        if (member == null){
            throw new UsernameNotFoundException("아이디가 존재하지 않습니다");
        }
        if (!passwordEncoder.matches(password, member.getPassword())){
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다");
        }
        CustomUserInfoDto memberInfoDto = modelMapper.map(member, CustomUserInfoDto.class);
        return jwtUtil.createAccessToken(memberInfoDto);
    }

    public String protectorLogin(LoginFormDto.ProtectorLoginFormDto form) {
        String code = form.getCode();
        Member member =memberRepository.findByCode(code);
        if (member == null) {
            throw new UsernameNotFoundException("코드가 존재하지 않습니다");
        }
        CustomUserInfoDto memberInfoDto = modelMapper.map(member, CustomUserInfoDto.class);
        return jwtUtil.createAccessToken(memberInfoDto);
    }
}
