package com.example.twoway_movie.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MemberDTO {

    private long usernum;

    // 아이디: 영문+숫자 4~20자
    @NotBlank(message = "아이디는 필수 입력입니다.")
    @Size(min = 4, max = 20, message = "아이디는 4~20자 이내로 입력하세요.")
    @Pattern(
            regexp = "^[a-zA-Z0-9]+$",
            message = "아이디는 영문과 숫자만 사용 가능합니다."
    )
    private String userid;

    // 비밀번호: 영문+숫자+특수문자 포함 8~20자
    @NotBlank(message = "비밀번호는 필수 입력입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자 이내여야 합니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&]).+$",
            message = "비밀번호는 영문, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다."
    )
    private String userpw;

    // 이름: 한글 2~10자
    @NotBlank(message = "이름은 필수 입력입니다.")
    @Pattern(
            regexp = "^[가-힣]{2,10}$",
            message = "이름은 한글 2~10자로 입력해주세요."
    )
    private String username;

    // 전화번호: 숫자만 10~11자리
    @NotBlank(message = "전화번호는 필수 입력입니다.")
    @Pattern(
            regexp = "^010-\\d{4}-\\d{4}$",
            message = "전화번호는 010-0000-0000 형식으로 입력해주세요."
    )
    private String usertell;

}
