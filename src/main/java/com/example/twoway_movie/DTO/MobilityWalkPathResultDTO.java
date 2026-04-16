package com.example.twoway_movie.DTO;

import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MobilityWalkPathResultDTO {

    private boolean usedApi;
    private String message;

    private double walkDistanceKm;
    private int walkDurationMin;

    // ✅ 카카오 Polyline에 바로 넣을 좌표 리스트
    private List<MobilityPathPointDTO> pathPoints;
}
