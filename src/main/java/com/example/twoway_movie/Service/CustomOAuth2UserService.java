package com.example.twoway_movie.Service;

import com.example.twoway_movie.Entity.MemberEntity;
import com.example.twoway_movie.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {

        OAuth2User oauth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId(); // kakao / naver
        Map<String, Object> attrs = oauth2User.getAttributes();

        String providerIdTmp = null;
        String emailTmp = null;
        String nicknameTmp = null;

        if ("kakao".equals(provider)) {

            providerIdTmp = String.valueOf(attrs.get("id"));

            Map<String, Object> kakaoAccount = (Map<String, Object>) attrs.get("kakao_account");
            if (kakaoAccount != null) {
                Object emailObj = kakaoAccount.get("email");
                if (emailObj != null) emailTmp = String.valueOf(emailObj);

                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                if (profile != null) {
                    Object nickObj = profile.get("nickname");
                    if (nickObj != null) nicknameTmp = String.valueOf(nickObj);
                }
            }

        } else if ("naver".equals(provider)) {

            Map<String, Object> response = (Map<String, Object>) attrs.get("response");
            if (response == null) throw new IllegalArgumentException("네이버 response가 null 입니다.");

            providerIdTmp = String.valueOf(response.get("id"));

            Object emailObj = response.get("email");
            if (emailObj != null) emailTmp = String.valueOf(emailObj);

            Object nameObj = response.get("name");
            if (nameObj != null) nicknameTmp = String.valueOf(nameObj);

        } else {
            throw new IllegalArgumentException("지원하지 않는 소셜 로그인: " + provider);
        }

        // ✅ 내부 userid 통일
        final String socialUserid = provider + "_" + providerIdTmp;

        // ✅ null 방지(카카오에서 종종 null)
        String email = (emailTmp == null || emailTmp.isBlank()) ? (socialUserid + "@social.local") : emailTmp;
        String nickname = (nicknameTmp == null || nicknameTmp.isBlank()) ? socialUserid : nicknameTmp;

        // ✅ 회원 조회
        MemberEntity member = memberRepository.findByUserid(socialUserid);

        if (member == null) {
            member = new MemberEntity();

            member.setUserid(socialUserid);
            member.setUserpw(null);
            member.setUsertell(null);

            // 신규만 기본 권한 세팅 (⭐️ 기존 role 덮어쓰면 안됨)
            member.setRole("ROLE_USER");
        }

        // ✅ (핵심) 신규/기존 상관없이 소셜 정보 저장/갱신
        member.setUsername(nickname);      // 화면 표시 이름
        member.setProvider(provider);
        member.setProviderId(providerIdTmp);
        member.setEmail(email);
        member.setNickname(nickname);

        memberRepository.save(member);

        // ✅ 권한
        List<GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority(member.getRole()));

        // ✅ 화면용 attributes
        Map<String, Object> newAttrs = new HashMap<>();
        newAttrs.put("userid", member.getUserid());
        newAttrs.put("username", member.getUsername());
        newAttrs.put("role", member.getRole());
        newAttrs.put("email", member.getEmail());
        newAttrs.put("nickname", member.getNickname());
        newAttrs.put("provider", member.getProvider());

        return new DefaultOAuth2User(authorities, newAttrs, "username");
    }
}
