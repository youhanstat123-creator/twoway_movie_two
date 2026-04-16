package com.example.twoway_movie.Service.Member;

import com.example.twoway_movie.DTO.MemberDTO;
import com.example.twoway_movie.Entity.MemberEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MemberService {

    void memberinsert(MemberDTO memberDTO);

    Page<MemberEntity> entitypage(int page);

    // ✅ 검색
    List<MemberEntity> search(String mkey, String mvalue);
}
