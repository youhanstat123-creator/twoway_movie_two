package com.example.twoway_movie.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// ✅ 추가 import
import com.example.twoway_movie.Service.CustomOAuth2UserService;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    private final UserDetailsService userDetailsService;

    // ✅ 추가: 카카오/네이버 OAuth2UserService
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authenticationProvider(authenticationProvider())

                .authorizeHttpRequests(auth -> auth
                        // ✅ 정적 리소스
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/img/**", "/image/**",
                                "/favicon.ico", "/upload/**").permitAll()

                        // ✅ 업로드 이미지
                        .requestMatchers("/upload/**").permitAll()

                        // ✅ 공개 페이지
                        .requestMatchers("/", "/login", "/loginprocess", "/member_inputgo", "/memberinputsave").permitAll()

                        // ✅ (추가) OAuth2 로그인 시작/콜백 경로는 공개해두는 게 안전
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()

                        // ✅ 모빌리티(현재 공개)
                        .requestMatchers("/mobility/**").permitAll()

                        // (구버전/레거시 경로가 남아있다면 유지해도 됨)
                        .requestMatchers("/movie/image/**").permitAll()

                        // ✅ ADMIN 전용
                        .requestMatchers("/member_outgo").hasRole("ADMIN")
                        .requestMatchers("/member_searchgo", "/membersearchsave").hasRole("ADMIN")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/mv_inputgo", "/mv_inputsave", "/update", "/updateSave", "/delete").hasRole("ADMIN")

                        // 나머지
                        .anyRequest().authenticated()
                )

                // ✅ 기존 폼 로그인 유지
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/loginprocess")
                        .usernameParameter("userid")
                        .passwordParameter("userpw")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )

                // ✅ (추가) 카카오/네이버 OAuth2 로그인
                .oauth2Login(oauth -> oauth
                        .loginPage("/login") // 로그인 페이지는 동일하게 사용
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService) // 여기서 DB 저장/업데이트 처리
                        )
                        .defaultSuccessUrl("/", true) // 소셜 로그인 성공 후 이동
                        .failureUrl("/login?error=true")
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}
