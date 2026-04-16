package com.example.twoway_movie.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Data
@AllArgsConstructor
@Table(name = "TWOWAY_MOVIE_MEMBER")
@SequenceGenerator(
        name = "member",
        sequenceName = "member_seq",
        allocationSize = 1,
        initialValue = 1000
)
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member")
    private long usernum;

    // 로그인 ID (폼: userid / 소셜: kakao_1234)
    @Column(unique = true)
    private String userid;

    // 폼 로그인만 사용
    private String userpw;

    // 화면에 보여줄 이름
    private String username;

    private String usertell;

    // ROLE_USER / ROLE_ADMIN
    private String role;

    // ===== 소셜 로그인 전용 =====
    private String provider;      // kakao / naver
    private String providerId;    // 소셜에서 내려주는 id
    private String email;
    private String nickname;
}
