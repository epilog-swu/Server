package com.epi.epilog.global.utils;

import com.epi.epilog.app.dto.CustomUserInfoDto;
import com.epi.epilog.app.repository.MemberRepository;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findById(username).orElseThrow(()->new UsernameNotFoundException("사용자가 존재하지 않습니다"));
        CustomUserInfoDto infoDto = modelMapper.map(member, CustomUserInfoDto.class);
        return new CustomUserDetails(infoDto);
    }
}
