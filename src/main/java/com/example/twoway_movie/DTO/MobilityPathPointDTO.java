package com.example.twoway_movie.DTO;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MobilityPathPointDTO {
    private double lat;
    private double lng;
}
