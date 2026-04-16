package com.example.twoway_movie.Service.fee;

import com.example.twoway_movie.DTO.fee.FeeDtos.*;
import com.example.twoway_movie.Entity.CertCode;
import com.example.twoway_movie.Repository.CertCodeRepository;
import com.example.twoway_movie.Repository.ParkingSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CertService {

    private final ParkingSessionRepository sessionRepo;
    private final CertCodeRepository certRepo;

    private static final SecureRandom RND = new SecureRandom();

    @Transactional
    public IssueRes issue(IssueReq req) {
        if (req.sessionId == null) throw new IllegalArgumentException("sessionId 필요");

        // 세션 존재 확인
        sessionRepo.findById(req.sessionId).orElseThrow(() -> new IllegalArgumentException("세션 없음"));

        String code = "2WAY-" + randomHex(8); // 8자리
        LocalDateTime now = LocalDateTime.now();

        CertCode c = CertCode.builder()
                .code(code)
                .sessionId(req.sessionId)
                .issuedAt(now)
                .expiresAt(now.plusMinutes(30))
                .status("ISSUED")
                .build();

        certRepo.save(c);

        return IssueRes.builder()
                .code(code)
                .expiresAt(c.getExpiresAt())
                .build();
    }

    @Transactional(readOnly = true)
    public VerifyRes verify(VerifyReq req) {
        if (req.code == null || req.code.trim().isEmpty()) {
            return VerifyRes.builder().status("NOT_FOUND").message("코드 입력 필요").build();
        }

        var opt = certRepo.findById(req.code.trim());
        if (opt.isEmpty()) return VerifyRes.builder().status("NOT_FOUND").message("코드 없음").build();

        var c = opt.get();

        // 세션 매칭
        if (req.sessionId != null && c.getSessionId() != null && !c.getSessionId().equals(req.sessionId)) {
            return VerifyRes.builder().status("MISMATCH").message("세션 불일치").build();
        }

        // 만료
        if (c.getExpiresAt() != null && c.getExpiresAt().isBefore(LocalDateTime.now())) {
            return VerifyRes.builder().status("EXPIRED").message("만료됨").build();
        }

        // 사용됨
        if ("USED".equals(c.getStatus())) {
            return VerifyRes.builder().status("ALREADY_USED").message("이미 사용됨").build();
        }

        return VerifyRes.builder().status("OK").message("인증 가능").build();
    }

    private static String randomHex(int len) {
        byte[] b = new byte[len / 2];
        RND.nextBytes(b);
        StringBuilder sb = new StringBuilder();
        for (byte x : b) sb.append(String.format("%02X", x));
        return sb.toString();
    }
}
