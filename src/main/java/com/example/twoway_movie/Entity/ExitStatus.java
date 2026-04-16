package com.example.twoway_movie.Entity;

public enum ExitStatus {
    IN_PARKING,     // 주차중
    CALCULATED,     // 정산 계산 완료(확정 아님)
    APPROVED,       // 출차 승인
    EXITED,         // 출차 완료
    CANCELLED       // 취소
}
