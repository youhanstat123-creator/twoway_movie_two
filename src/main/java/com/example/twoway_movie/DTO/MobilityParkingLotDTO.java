package com.example.twoway_movie.DTO;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MobilityParkingLotDTO {
    private String id;
    private String name;
    private String address;

    private double lat;
    private double lng;

    private double distanceKm; // cinema 기준
}
