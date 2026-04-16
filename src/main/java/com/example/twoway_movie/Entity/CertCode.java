package com.example.twoway_movie.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name="PARKING_CERT_CODE",
        indexes = {
                @Index(name="IDX_CERT_SESSION", columnList = "SESSION_ID"),
                @Index(name="IDX_CERT_EXPIRES", columnList = "EXPIRES_AT")
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CertCode {

    @Id
    @Column(name="CODE", length=60)
    private String code; // 예: 2WAY-811C2EFC

    @Column(name="SESSION_ID")
    private Long sessionId;

    @Column(name="ISSUED_AT")
    private LocalDateTime issuedAt;

    @Column(name="EXPIRES_AT")
    private LocalDateTime expiresAt;

    @Column(name="USED_AT")
    private LocalDateTime usedAt;

    @Column(name="STATUS", length=20)
    private String status; // ISSUED / USED / EXPIRED
}
