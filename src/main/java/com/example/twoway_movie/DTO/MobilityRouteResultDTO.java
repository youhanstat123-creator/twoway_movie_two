package com.example.twoway_movie.DTO;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MobilityRouteResultDTO {
    private boolean usedApi;
    private String message;

    private double distanceKm;
    private int durationMin;
    private int taxiFareWon;

    private String externalMapUrl;
}
