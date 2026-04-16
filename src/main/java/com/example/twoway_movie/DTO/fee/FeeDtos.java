package com.example.twoway_movie.DTO.fee;

import lombok.*;

import java.time.LocalDateTime;

public class FeeDtos {

    @Getter @Setter
    public static class CalculateReq {
        public Long sessionId;            // 있으면 업데이트, 없으면 생성
        public String carNo;
        public String parkingId;
        public String parkingName;
        public LocalDateTime entryAt;
        public LocalDateTime exitPlannedAt;
        public String certCode;           // 선택
    }

    @Getter @Builder
    public static class CalculateRes {
        public Long sessionId;
        public String exitStatus;
        public String payStatus;

        public Integer totalMinutes;
        public Integer freeMinutes;
        public Integer feeBefore;
        public Integer discountAmount;
        public Integer finalFee;

        public boolean certApplied;
        public String certStatus;         // OK / NOT_FOUND / EXPIRED / ALREADY_USED / NONE
        public String certCodeUsed;

        public boolean canApproveExit;    // 출차승인 가능 여부
        public String message;
    }

    @Getter @Setter
    public static class IssueReq {
        public Long sessionId;
    }

    @Getter @Builder
    public static class IssueRes {
        public String code;
        public LocalDateTime expiresAt;
    }

    @Getter @Setter
    public static class VerifyReq {
        public Long sessionId;
        public String code;
    }

    @Getter @Builder
    public static class VerifyRes {
        public String status;    // OK / NOT_FOUND / EXPIRED / ALREADY_USED / MISMATCH
        public String message;
    }

    @Getter @Setter
    public static class ApproveReq {
        public Long sessionId;
    }

    @Getter @Builder
    public static class ApproveRes {
        public String exitStatus;
        public LocalDateTime approvedAt;
        public String message;
    }

    @Getter @Setter
    public static class ExitCompleteReq {
        public Long sessionId;
    }

    @Getter @Builder
    public static class ExitCompleteRes {
        public String exitStatus;
        public LocalDateTime exitedAt;
        public String message;
    }
}
