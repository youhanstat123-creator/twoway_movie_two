package com.example.twoway_movie.DTO;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MobilityGeoDTO {
    private boolean usedApi;
    private String message;

    private double lat;
    private double lng;
}
