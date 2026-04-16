package com.example.twoway_movie.DTO;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MobilityRouteRequestDTO {
    private String originDistrict;  // 출발지(인천 구)
    private String cinemaId;        // 영화관 id
    private String cinemaName;      // 영화관 이름(지도 링크용)
}
