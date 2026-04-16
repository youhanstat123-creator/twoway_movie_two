package com.example.twoway_movie.Service.mobility;

import com.example.twoway_movie.DTO.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class MobilityServiceImpl implements MobilityService {

    @Value("${naver.map.client-id:}")
    private String naverClientId;

    @Value("${naver.map.client-secret:}")
    private String naverClientSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper om = new ObjectMapper();

    // ✅ 극장 주소(고정)
    public static final String CINEMA_ADDRESS = "인천광역시 부평구 광장로 16";

    // ✅ fallback 좌표(부평역 인근) - 지오코딩/경로 실패해도 지도 표시용
    private static final double FALLBACK_LAT = 37.4896;
    private static final double FALLBACK_LNG = 126.7233;

    // =========================
    // Step1: 구(인천) → 영화관 (Fallback 계산)
    // =========================
    @Override
    public MobilityRouteResultDTO route(MobilityRouteRequestDTO req) {

        double[] origin = districtCenter(req.getOriginDistrict());
        double[] cinema = cinemaPoint(req.getCinemaId());

        double km = haversineKm(origin[0], origin[1], cinema[0], cinema[1]);
        int min = estimateDriveMinutes(km);
        int fare = estimateTaxiFareWon(km);

        String q = (req.getCinemaName() == null || req.getCinemaName().isBlank()) ? "영화관" : req.getCinemaName();
        String mapUrl = "https://map.kakao.com/link/search/" + URLEncoder.encode(q, StandardCharsets.UTF_8);

        return MobilityRouteResultDTO.builder()
                .usedApi(false)
                .message("Fallback(직선거리/평균속도/추정요금)")
                .distanceKm(round2(km))
                .durationMin(min)
                .taxiFareWon(fare)
                .externalMapUrl(mapUrl)
                .build();
    }

    // =========================
    // Step2: 영화관 기준 5km 공영주차장 (더미)
    // =========================
    @Override
    public MobilityParkingResultDTO parkingWithin5km(String cinemaId) {

        double[] c = cinemaPoint(cinemaId);
        double lat = c[0];
        double lng = c[1];

        List<MobilityParkingLotDTO> list = List.of(
                MobilityParkingLotDTO.builder()
                        .id("P1").name("부평역 공영주차장")
                        .address("인천광역시 부평구 광장로 4")
                        .lat(lat + 0.010).lng(lng + 0.006)
                        .build(),

                MobilityParkingLotDTO.builder()
                        .id("P2").name("부평문화의거리 공영주차장")
                        .address("인천광역시 부평구 부평동 201-1")
                        .lat(lat - 0.012).lng(lng + 0.004)
                        .build(),

                MobilityParkingLotDTO.builder()
                        .id("P3").name("부평남부역 공영주차장")
                        .address("인천광역시 부평구 부평동 400-2")
                        .lat(lat + 0.007).lng(lng - 0.010)
                        .build()
        );

        for (MobilityParkingLotDTO p : list) {
            double km = haversineKm(lat, lng, p.getLat(), p.getLng());
            p.setDistanceKm(round2(km));
        }

        return MobilityParkingResultDTO.builder()
                .usedApi(false)
                .message("Fallback 주차장 목록(더미)")
                .parkingLots(list)
                .build();
    }

    // =========================
    // Step3-a: 도보거리/시간(값만) - Fallback
    // =========================
    @Override
    public MobilityWalkResultDTO walk(MobilityWalkRequestDTO req) {
        double km = haversineKm(req.getParkingLat(), req.getParkingLng(), req.getCinemaLat(), req.getCinemaLng());
        int min = estimateWalkMinutes(km);

        return MobilityWalkResultDTO.builder()
                .usedApi(false)
                .message("Fallback(직선거리/평균보행속도)")
                .walkDistanceKm(round2(km))
                .walkDurationMin(min)
                .mode("FALLBACK")
                .pathPoints(List.of(
                        new double[]{req.getParkingLat(), req.getParkingLng()},
                        new double[]{req.getCinemaLat(), req.getCinemaLng()}
                ))
                .build();
    }

    // =========================
    // Step3-b: ✅ 라인(Polyline) - Directions5 Driving ONLY
    // =========================
    @Override
    public MobilityWalkResultDTO walkPath(MobilityWalkRequestDTO req) {

        // 키 없으면 fallback
        if (naverClientId == null || naverClientId.isBlank() ||
                naverClientSecret == null || naverClientSecret.isBlank()) {
            return straightFallback(req, "Directions5 키 없음 → Fallback 직선 라인");
        }

        try {
            return callDirections5Driving(req);
        } catch (Exception e) {
            return straightFallback(req, "Directions5(DRIVING) 실패 → Fallback 직선 라인");
        }
    }

    private MobilityWalkResultDTO callDirections5Driving(MobilityWalkRequestDTO req) throws Exception {

        // Directions5는 start/goal을 "lng,lat"
        String start = req.getParkingLng() + "," + req.getParkingLat();
        String goal  = req.getCinemaLng() + "," + req.getCinemaLat();

        // ✅ 너가 올린 문서 화면 기준: /map-direction/v1/driving
        String url = "https://maps.apigw.ntruss.com/map-direction/v1/driving"
                + "?start=" + URLEncoder.encode(start, StandardCharsets.UTF_8)
                + "&goal="  + URLEncoder.encode(goal, StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", naverClientId);
        headers.set("X-NCP-APIGW-API-KEY", naverClientSecret);

        ResponseEntity<String> resp =
                restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        JsonNode root = om.readTree(resp.getBody());
        JsonNode traoptimal = root.path("route").path("traoptimal");

        if (!traoptimal.isArray() || traoptimal.isEmpty()) {
            throw new IllegalStateException("route.traoptimal 없음");
        }

        JsonNode route = traoptimal.get(0);
        JsonNode pathArr = route.path("path");
        if (!pathArr.isArray() || pathArr.size() < 2) {
            throw new IllegalStateException("path 없음");
        }

        List<double[]> points = new ArrayList<>();
        for (JsonNode p : pathArr) {
            // p = [lng, lat]
            double lng = p.get(0).asDouble();
            double lat = p.get(1).asDouble();
            points.add(new double[]{lat, lng}); // 카카오맵은 (lat,lng) 쓰는 경우 많아서 이렇게 반환
        }

        JsonNode summary = route.path("summary");
        double km = summary.path("distance").asDouble(-1) / 1000.0;   // meters -> km
        int min = (int) Math.max(1, Math.round(summary.path("duration").asDouble(-1) / 60000.0)); // ms -> min

        // 혹시 summary가 비어있으면 fallback 계산
        if (km <= 0) {
            km = haversineKm(req.getParkingLat(), req.getParkingLng(), req.getCinemaLat(), req.getCinemaLng());
        }
        if (min <= 0) {
            min = estimateDriveMinutes(km);
        }

        return MobilityWalkResultDTO.builder()
                .usedApi(true)
                .mode("DRIVING")
                .message("Directions5 DRIVING 성공")
                .walkDistanceKm(round2(km))
                .walkDurationMin(min)
                .pathPoints(points)
                .build();
    }

    private MobilityWalkResultDTO straightFallback(MobilityWalkRequestDTO req, String msg) {

        double km = haversineKm(req.getParkingLat(), req.getParkingLng(), req.getCinemaLat(), req.getCinemaLng());
        int min = estimateWalkMinutes(km);

        return MobilityWalkResultDTO.builder()
                .usedApi(false)
                .mode("FALLBACK")
                .message(msg)
                .walkDistanceKm(round2(km))
                .walkDurationMin(min)
                .pathPoints(List.of(
                        new double[]{req.getParkingLat(), req.getParkingLng()},
                        new double[]{req.getCinemaLat(), req.getCinemaLng()}
                ))
                .build();
    }

    // =========================
    // Step4: 혼잡 TOP3 (Fallback)
    // =========================
    @Override
    public MobilityCongestionTop3DTO congestionTop3(String cinemaId) {
        return MobilityCongestionTop3DTO.builder()
                .usedApi(false)
                .message("Fallback 혼잡 패턴")
                .top3(List.of(
                        "평일 18:00~19:30 (퇴근 피크)",
                        "토 19:00~21:00 (주말 피크)",
                        "일 16:00~18:00 (가족 관람 피크)"
                ))
                .build();
    }

    // =========================
    // 주소→좌표 (네이버 Geocoding) : 기존처럼 유지
    // =========================
    @Override
    public MobilityGeoDTO geocode(String address) {
        // 지금은 키/연동 이슈가 많아서 “항상 fallback”으로 둬도 됨
        return MobilityGeoDTO.builder()
                .usedApi(false)
                .message("Geocoding 미사용(고정 좌표)")
                .lat(FALLBACK_LAT)
                .lng(FALLBACK_LNG)
                .build();
    }

    // =========================
    // 내부 좌표: 구 중심 / 영화관 좌표
    // =========================
    private double[] districtCenter(String district) {
        if (district == null) return new double[]{37.4563, 126.7052};
        return switch (district.trim()) {
            case "계양구" -> new double[]{37.5372, 126.7377};
            case "부평구" -> new double[]{37.5071, 126.7219};
            case "서구" -> new double[]{37.5455, 126.6768};
            case "남동구" -> new double[]{37.4473, 126.7313};
            case "연수구" -> new double[]{37.4103, 126.6783};
            case "미추홀구" -> new double[]{37.4636, 126.6506};
            case "중구" -> new double[]{37.4737, 126.6216};
            case "동구" -> new double[]{37.4739, 126.6435};
            case "강화군" -> new double[]{37.7465, 126.4881};
            case "옹진군" -> new double[]{37.4460, 126.6366};
            default -> new double[]{37.4563, 126.7052};
        };
    }

    private double[] cinemaPoint(String cinemaId) {
        // 샘플: C1=부평(고정), C2=계양 샘플
        if ("C2".equals(cinemaId)) return new double[]{37.5660, 126.7420};
        return new double[]{FALLBACK_LAT, FALLBACK_LNG};
    }

    // =========================
    // Utils
    // =========================
    private double haversineKm(double lat1, double lng1, double lat2, double lng2) {
        double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private int estimateDriveMinutes(double distanceKm) {
        double avgKmh = 28.0;
        return (int) Math.max(1, Math.round((distanceKm / avgKmh) * 60.0));
    }

    private int estimateWalkMinutes(double distanceKm) {
        double avgKmh = 4.5;
        return (int) Math.max(1, Math.round((distanceKm / avgKmh) * 60.0));
    }

    private int estimateTaxiFareWon(double distanceKm) {
        int base = 4800;
        int perKm = 1200;
        int fare = base + (int) Math.round(distanceKm * perKm);
        return Math.max(base, fare);
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
