package com.example.twoway_movie.Repository;

import com.example.twoway_movie.Entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    // ✅ 아이디 중복 체크
    boolean existsByUserid(String userid);

    // ✅ 로그인용 (Security에서 사용 가능)
    MemberEntity findByUserid(String userid);

    // ✅ 검색용 (빨간줄 났던 메서드들 전부 여기서 해결)
    List<MemberEntity> findByUseridContainingIgnoreCase(String value);

    List<MemberEntity> findByUsernameContainingIgnoreCase(String value);

    List<MemberEntity> findByUsertellContaining(String value);

    List<MemberEntity> findByRoleContainingIgnoreCase(String value);

    // ✅ 혹시 키가 이상하게 들어오면 전체 필드 OR 검색
    List<MemberEntity> findByUseridContainingIgnoreCaseOrUsernameContainingIgnoreCaseOrUsertellContainingOrRoleContainingIgnoreCase(
            String a, String b, String c, String d
    );
}
