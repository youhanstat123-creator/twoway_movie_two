package com.example.twoway_movie.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PARKING_SESSION")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ParkingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_PARKING_SESSION_GEN")
    @SequenceGenerator(name = "SEQ_PARKING_SESSION_GEN", sequenceName = "SEQ_PARKING_SESSION", allocationSize = 1)
    private Long id;

    @Column(name="CAR_NO", length=30)
    private String carNo;           // 차량번호(선택)

    @Column(name="PARKING_ID", length=20)
    private String parkingId;       // P1, P2...

    @Column(name="PARKING_NAME", length=100)
    private String parkingName;     // 공영주차장(P1) 등 표시용

    @Column(name="ENTRY_AT")
    private LocalDateTime entryAt;

    @Column(name="EXIT_PLANNED_AT")
    private LocalDateTime exitPlannedAt;

    // 계산 결과
    @Column(name="TOTAL_MINUTES")
    private Integer totalMinutes;

    @Column(name="FREE_MINUTES")
    private Integer freeMinutes;

    @Column(name="FEE_BEFORE")
    private Integer feeBefore;

    @Column(name="DISCOUNT_AMOUNT")
    private Integer discountAmount;

    @Column(name="FINAL_FEE")
    private Integer finalFee;

    @Column(name="CERT_CODE_USED", length=60)
    private String certCodeUsed;

    @Column(name="CERT_APPLIED")
    private Boolean certApplied;

    @Enumerated(EnumType.STRING)
    @Column(name="PAY_STATUS", length=20)
    private PayStatus payStatus;

    @Enumerated(EnumType.STRING)
    @Column(name="EXIT_STATUS", length=20)
    private ExitStatus exitStatus;

    @Column(name="EXIT_APPROVED_AT")
    private LocalDateTime exitApprovedAt;

    @Column(name="EXITED_AT")
    private LocalDateTime exitedAt;

    @Column(name="CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name="UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist(){
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        if (exitStatus == null) exitStatus = ExitStatus.IN_PARKING;
        if (payStatus == null) payStatus = PayStatus.NONE;
        if (certApplied == null) certApplied = false;
    }

    @PreUpdate
    void preUpdate(){
        updatedAt = LocalDateTime.now();
    }
}
