package com.example.twoway_movie.DTO;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MobilityWalkRequestDTO {
    private String cinemaId;

    private double parkingLat;
    private double parkingLng;

    private double cinemaLat;
    private double cinemaLng;
}
