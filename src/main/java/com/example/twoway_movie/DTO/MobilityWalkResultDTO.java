package com.example.twoway_movie.DTO;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MobilityWalkResultDTO {

    private boolean usedApi;          // 네이버 경로 성공 여부(걷기/차량 포함)
    private String message;           // 상태 메시지
    private double walkDistanceKm;    // 거리(km)
    private int walkDurationMin;      // 시간(분)

    // ✅ 추가: 어떤 모드로 라인 생성됐는지
    // WALKING / DRIVING / FALLBACK
    private String mode;

    // ✅ 추가: 지도 라인용 좌표 리스트 (각 원소: [lat, lng])
    private List<double[]> pathPoints;
}
