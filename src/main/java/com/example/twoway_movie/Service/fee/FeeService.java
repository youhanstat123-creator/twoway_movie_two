package com.example.twoway_movie.Service.fee;

import com.example.twoway_movie.DTO.fee.FeeDtos.*;
import com.example.twoway_movie.Entity.*;
import com.example.twoway_movie.Repository.CertCodeRepository;
import com.example.twoway_movie.Repository.ParkingSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FeeService {

    private final ParkingSessionRepository sessionRepo;
    private final CertCodeRepository certRepo;

    // 정책(시연용)
    private static final int UNIT_MIN = 30;
    private static final int UNIT_FEE = 500;
    private static final int FREE_MIN_IF_CERT = 180; // 3시간

    @Transactional
    public CalculateRes calculate(CalculateReq req) {
        ParkingSession s;

        if (req.sessionId != null) {
            s = sessionRepo.findById(req.sessionId)
                    .orElseThrow(() -> new IllegalArgumentException("세션 없음: " + req.sessionId));
        } else {
            s = ParkingSession.builder().build();
        }

        // 입력 반영
        s.setCarNo(safe(req.carNo));
        s.setParkingId(safe(req.parkingId));
        s.setParkingName(safe(req.parkingName));
        s.setEntryAt(req.entryAt);
        s.setExitPlannedAt(req.exitPlannedAt);

        if (s.getEntryAt() == null || s.getExitPlannedAt() == null) {
            throw new IllegalArgumentException("입차시간/출차예정은 필수");
        }
        if (s.getExitPlannedAt().isBefore(s.getEntryAt())) {
            throw new IllegalArgumentException("출차예정이 입차시간보다 빠를 수 없음");
        }

        int totalMin = (int) Duration.between(s.getEntryAt(), s.getExitPlannedAt()).toMinutes();
        if (totalMin < 1) totalMin = 1;

        // 인증 검증(선택)
        boolean certApplied = false;
        String certStatus = "NONE";
        String certUsed = null;

        if (req.certCode != null && !req.certCode.trim().isEmpty()) {
            var vr = verifyInternal(req.sessionId, req.certCode.trim());
            certStatus = vr.status;
            if ("OK".equals(vr.status)) {
                certApplied = true;
                certUsed = req.certCode.trim();
            }
        }

        int freeMin = certApplied ? FREE_MIN_IF_CERT : 0;

        // 할인 전 금액 (총시간 기준)
        int feeBefore = calcFee(totalMin);

        // 최종 결제 (무료시간 차감 후)
        int payMin = Math.max(0, totalMin - freeMin);
        int finalFee = calcFee(payMin);

        int discount = Math.max(0, feeBefore - finalFee);

        // 세션 저장(여기서 상태는 CALCULATED)
        s.setTotalMinutes(totalMin);
        s.setFreeMinutes(freeMin);
        s.setFeeBefore(feeBefore);
        s.setFinalFee(finalFee);
        s.setDiscountAmount(discount);

        s.setCertApplied(certApplied);
        s.setCertCodeUsed(certUsed);

        s.setExitStatus(ExitStatus.CALCULATED);
        s.setPayStatus(certApplied ? PayStatus.WAIVED : PayStatus.NONE);

        s = sessionRepo.save(s);

        boolean canApprove = (s.getFinalFee() != null && s.getFinalFee() == 0)
                || (s.getPayStatus() == PayStatus.PAID) || (s.getPayStatus() == PayStatus.WAIVED);

        return CalculateRes.builder()
                .sessionId(s.getId())
                .exitStatus(s.getExitStatus().name())
                .payStatus(s.getPayStatus().name())
                .totalMinutes(totalMin)
                .freeMinutes(freeMin)
                .feeBefore(feeBefore)
                .discountAmount(discount)
                .finalFee(finalFee)
                .certApplied(certApplied)
                .certStatus(certStatus)
                .certCodeUsed(certUsed)
                .canApproveExit(canApprove)
                .message(canApprove ? "정산 완료. 출차 승인 가능합니다." : "정산 완료. (인증/결제 필요)")
                .build();
    }

    @Transactional
    public ApproveRes approveExit(ApproveReq req) {
        var s = sessionRepo.findById(req.sessionId)
                .orElseThrow(() -> new IllegalArgumentException("세션 없음"));

        if (s.getExitStatus() != ExitStatus.CALCULATED) {
            return ApproveRes.builder()
                    .exitStatus(s.getExitStatus().name())
                    .approvedAt(s.getExitApprovedAt())
                    .message("출차 승인은 '정산 완료' 상태에서만 가능합니다.")
                    .build();
        }

        boolean ok = (s.getFinalFee() != null && s.getFinalFee() == 0)
                || (s.getPayStatus() == PayStatus.PAID) || (s.getPayStatus() == PayStatus.WAIVED);

        if (!ok) {
            return ApproveRes.builder()
                    .exitStatus(s.getExitStatus().name())
                    .approvedAt(null)
                    .message("출차 승인 불가: 인증/결제 후 가능합니다.")
                    .build();
        }

        // ✅ 인증코드가 적용되었다면 여기서 USED 확정(핵심)
        if (Boolean.TRUE.equals(s.getCertApplied()) && s.getCertCodeUsed() != null) {
            var code = certRepo.findById(s.getCertCodeUsed()).orElse(null);
            if (code != null) {
                // 세션 매칭 확인
                if (code.getSessionId() != null && !code.getSessionId().equals(s.getId())) {
                    return ApproveRes.builder()
                            .exitStatus(s.getExitStatus().name())
                            .approvedAt(null)
                            .message("인증코드 세션 불일치. 다시 발급/검증하세요.")
                            .build();
                }
                // 만료/사용 체크
                if (code.getExpiresAt() != null && code.getExpiresAt().isBefore(LocalDateTime.now())) {
                    code.setStatus("EXPIRED");
                    certRepo.save(code);
                    return ApproveRes.builder()
                            .exitStatus(s.getExitStatus().name())
                            .approvedAt(null)
                            .message("인증코드 만료. 다시 발급하세요.")
                            .build();
                }
                if ("USED".equals(code.getStatus())) {
                    return ApproveRes.builder()
                            .exitStatus(s.getExitStatus().name())
                            .approvedAt(null)
                            .message("인증코드 이미 사용됨. 다시 발급하세요.")
                            .build();
                }

                code.setStatus("USED");
                code.setUsedAt(LocalDateTime.now());
                code.setSessionId(s.getId());
                certRepo.save(code);
            }
        }

        s.setExitStatus(ExitStatus.APPROVED);
        s.setExitApprovedAt(LocalDateTime.now());
        sessionRepo.save(s);

        return ApproveRes.builder()
                .exitStatus(s.getExitStatus().name())
                .approvedAt(s.getExitApprovedAt())
                .message("출차 승인 완료(게이트 오픈 가능 상태).")
                .build();
    }

    @Transactional
    public ExitCompleteRes exitComplete(ExitCompleteReq req) {
        var s = sessionRepo.findById(req.sessionId)
                .orElseThrow(() -> new IllegalArgumentException("세션 없음"));

        if (s.getExitStatus() != ExitStatus.APPROVED) {
            return ExitCompleteRes.builder()
                    .exitStatus(s.getExitStatus().name())
                    .exitedAt(s.getExitedAt())
                    .message("출차 완료는 '승인' 이후에만 가능합니다.")
                    .build();
        }

        s.setExitStatus(ExitStatus.EXITED);
        s.setExitedAt(LocalDateTime.now());
        sessionRepo.save(s);

        return ExitCompleteRes.builder()
                .exitStatus(s.getExitStatus().name())
                .exitedAt(s.getExitedAt())
                .message("출차 완료 처리되었습니다.")
                .build();
    }

    // ===== 인증 검증 내부(정산 시에는 USED 처리하지 않음) =====
    private VerifyRes verifyInternal(Long sessionId, String code) {
        var opt = certRepo.findById(code);
        if (opt.isEmpty()) {
            return VerifyRes.builder().status("NOT_FOUND").message("코드 없음").build();
        }
        var c = opt.get();

        // 세션 매칭: 발급 시 sessionId로 묶이므로 여기서 체크
        if (sessionId != null && c.getSessionId() != null && !c.getSessionId().equals(sessionId)) {
            return VerifyRes.builder().status("MISMATCH").message("세션 불일치").build();
        }

        // 만료
        if (c.getExpiresAt() != null && c.getExpiresAt().isBefore(LocalDateTime.now())) {
            c.setStatus("EXPIRED");
            certRepo.save(c);
            return VerifyRes.builder().status("EXPIRED").message("만료됨").build();
        }

        // 이미 사용
        if ("USED".equals(c.getStatus())) {
            return VerifyRes.builder().status("ALREADY_USED").message("이미 사용됨").build();
        }

        return VerifyRes.builder().status("OK").message("인증 가능").build();
    }

    private static int calcFee(int minutes) {
        if (minutes <= 0) return 0;
        int units = (minutes + UNIT_MIN - 1) / UNIT_MIN; // ceil
        return units * UNIT_FEE;
    }

    private static String safe(String s) {
        return (s == null) ? null : s.trim();
    }
}
