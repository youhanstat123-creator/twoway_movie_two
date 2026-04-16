package com.example.twoway_movie.Controller;

import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/mobility/memapi") // ✅ 충돌 방지: /mobility/api -> /mobility/memapi
public class MobilityCertApiController {

    private static final ConcurrentHashMap<String, CertRecord> STORE = new ConcurrentHashMap<>();

    @PostMapping("/cert/issue")
    public Map<String, Object> issue() {
        String code = "2WAY-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        Instant now = Instant.now();
        Instant exp = now.plus(30, ChronoUnit.MINUTES);

        STORE.put(code, new CertRecord(code, now.toEpochMilli(), exp.toEpochMilli(), false));
        return Map.of(
                "ok", true,
                "certCode", code,
                "expiresAt", exp.toEpochMilli(),
                "ttlMin", 30
        );
    }

    @PostMapping("/cert/verify")
    public Map<String, Object> verify(@RequestBody Map<String, String> req) {
        String code = req.getOrDefault("code", "").trim().toUpperCase();

        if (code.isEmpty()) return Map.of("ok", false, "reason", "EMPTY");

        CertRecord r = STORE.get(code);
        if (r == null) return Map.of("ok", false, "reason", "NOT_FOUND");

        long now = Instant.now().toEpochMilli();
        if (r.used) return Map.of("ok", false, "reason", "ALREADY_USED");
        if (now > r.expiresAt) return Map.of("ok", false, "reason", "EXPIRED");

        return Map.of("ok", true, "reason", "OK", "expiresAt", r.expiresAt);
    }

    @PostMapping("/cert/use")
    public Map<String, Object> use(@RequestBody Map<String, String> req) {
        String code = req.getOrDefault("code", "").trim().toUpperCase();
        CertRecord r = STORE.get(code);
        if (r == null) return Map.of("ok", false, "reason", "NOT_FOUND");

        long now = Instant.now().toEpochMilli();
        if (r.used) return Map.of("ok", false, "reason", "ALREADY_USED");
        if (now > r.expiresAt) return Map.of("ok", false, "reason", "EXPIRED");

        r.used = true;
        return Map.of("ok", true, "reason", "USED");
    }

    static class CertRecord {
        String code;
        long issuedAt;
        long expiresAt;
        volatile boolean used;

        CertRecord(String code, long issuedAt, long expiresAt, boolean used) {
            this.code = code;
            this.issuedAt = issuedAt;
            this.expiresAt = expiresAt;
            this.used = used;
        }
    }
}
