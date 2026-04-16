package com.example.twoway_movie.Entity;

public enum PayStatus {
    NONE,       // 결제/면제 전
    WAIVED,     // 인증으로 면제(무료)
    PAID        // 결제 완료(이번 프로젝트에서는 옵션)
}
