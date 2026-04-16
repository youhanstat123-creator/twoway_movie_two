package com.example.twoway_movie.Service.mobility;

import com.example.twoway_movie.DTO.*;

public interface MobilityService {

    MobilityRouteResultDTO route(MobilityRouteRequestDTO req);

    MobilityParkingResultDTO parkingWithin5km(String cinemaId);

    MobilityWalkResultDTO walk(MobilityWalkRequestDTO req);         // 거리/시간(기존)
    MobilityWalkResultDTO walkPath(MobilityWalkRequestDTO req);     // ✅ 도로 Polyline 포함(신규)

    MobilityCongestionTop3DTO congestionTop3(String cinemaId);

    MobilityGeoDTO geocode(String address);
}
