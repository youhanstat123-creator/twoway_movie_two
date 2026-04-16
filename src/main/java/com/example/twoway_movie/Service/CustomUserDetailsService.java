package com.example.twoway_movie.Service;


import com.example.twoway_movie.Entity.MemberEntity;
import com.example.twoway_movie.Repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Slf4j @Service  @AllArgsConstructor
    public class CustomUserDetailsService implements UserDetailsService {
        //회원정보를 담은 인터페이스를 상속받음
        private final MemberRepository memberRepository;
        @Override
        @Transactional(readOnly = true)
        public UserDetails loadUserByUsername(String userid) {//회원정보를 담는
            Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
            //GrantedAuthority: 현재 사용자의 권한 admin or user를 role로 표시(role=역할)
            log.info("로그인 시도 userid = {}", userid);
            MemberEntity memberEntity = memberRepository.findByUserid(userid);
            //findOneById의 정보를 가져와서 user에 담음
            if (memberEntity == null) {
                throw new UsernameNotFoundException("존재하지 않는 사용자");
            }
            return User.builder()
                    .username(memberEntity.getUserid())
                    .password(memberEntity.getUserpw())
                    .roles(memberEntity.getRole().replace("ROLE_", ""))
                    // ROLE_ADMIN → ADMIN
                    .build();
            }
        }

