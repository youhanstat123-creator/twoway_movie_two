package com.example.twoway_movie.Service.Member;

import com.example.twoway_movie.DTO.MemberDTO;
import com.example.twoway_movie.Entity.MemberEntity;
import com.example.twoway_movie.Repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberServiceImp implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void memberinsert(MemberDTO memberDTO) {

        if (memberRepository.existsByUserid(memberDTO.getUserid())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        MemberEntity me = new MemberEntity();
        me.setUserid(memberDTO.getUserid());
        me.setUserpw(passwordEncoder.encode(memberDTO.getUserpw()));
        me.setUsername(memberDTO.getUsername());
        me.setUsertell(memberDTO.getUsertell());

        String uid = memberDTO.getUserid();
        if (uid != null && uid.trim().equalsIgnoreCase("admin")) {
            me.setRole("ROLE_ADMIN");
        } else {
            me.setRole("ROLE_USER");
        }

        memberRepository.save(me);
    }

    @Override
    public Page<MemberEntity> entitypage(int page) {
        return memberRepository.findAll(PageRequest.of(page, 5));
    }

    @Override
    public List<MemberEntity> search(String mkey, String mvalue) {

        String key = (mkey == null) ? "userid" : mkey.trim().toLowerCase();
        String value = (mvalue == null) ? "" : mvalue.trim();

        // ✅ 빈 값이면 전체 반환(검색창만 눌러도 동작)
        if (value.isEmpty()) {
            return memberRepository.findAll();
        }

        // ✅ 키별 검색
        return switch (key) {
            case "userid" -> memberRepository.findByUseridContainingIgnoreCase(value);
            case "username" -> memberRepository.findByUsernameContainingIgnoreCase(value);
            case "usertell", "phone", "tell" -> memberRepository.findByUsertellContaining(value);
            case "role" -> memberRepository.findByRoleContainingIgnoreCase(value);

            // ✅ 키가 이상하면 전체 필드 OR 검색
            default -> memberRepository
                    .findByUseridContainingIgnoreCaseOrUsernameContainingIgnoreCaseOrUsertellContainingOrRoleContainingIgnoreCase(
                            value, value, value, value
                    );
        };
    }
}
