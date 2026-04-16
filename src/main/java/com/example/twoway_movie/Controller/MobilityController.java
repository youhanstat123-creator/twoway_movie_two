package com.example.twoway_movie.Controller;

import com.example.twoway_movie.DTO.*;
import com.example.twoway_movie.Service.mobility.MobilityService;
import com.example.twoway_movie.Service.mobility.MobilityServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class MobilityController {

    private final MobilityService mobilityService;

    @Value("${kakao.javascript.key:}")
    private String kakaoJsKey;

    @GetMapping("/mobility")
    public String mobilityHome(Model model) {

        model.addAttribute("districts", new String[]{
                "계양구","부평구","서구","남동구","연수구","미추홀구","중구","동구","강화군","옹진군"
        });

        model.addAttribute("cinemas", new String[][]{
                {"C1","2WAY CINEMA (부평 샘플)"},
                {"C2","2WAY CINEMA (계양 샘플)"}
        });

        model.addAttribute("kakaoJsKey", kakaoJsKey);
        model.addAttribute("cinemaAddress", MobilityServiceImpl.CINEMA_ADDRESS);

        return "mobility/mobility_home";
    }

    @PostMapping("/mobility/api/route")
    @ResponseBody
    public ResponseEntity<MobilityRouteResultDTO> route(@RequestBody MobilityRouteRequestDTO req) {
        return ResponseEntity.ok(mobilityService.route(req));
    }

    @GetMapping("/mobility/api/parking")
    @ResponseBody
    public ResponseEntity<MobilityParkingResultDTO> parking(@RequestParam String cinemaId) {
        return ResponseEntity.ok(mobilityService.parkingWithin5km(cinemaId));
    }

    @PostMapping("/mobility/api/walk")
    @ResponseBody
    public ResponseEntity<MobilityWalkResultDTO> walk(@RequestBody MobilityWalkRequestDTO req) {
        return ResponseEntity.ok(mobilityService.walk(req));
    }

    // ✅ Step3: 실제 도로 라인 포함 경로
    @PostMapping("/mobility/api/walk/path")
    @ResponseBody
    public ResponseEntity<MobilityWalkResultDTO> walkPath(@RequestBody MobilityWalkRequestDTO req) {
        return ResponseEntity.ok(mobilityService.walkPath(req));
    }

    @GetMapping("/mobility/api/congestion/top3")
    @ResponseBody
    public ResponseEntity<MobilityCongestionTop3DTO> congestion(@RequestParam String cinemaId) {
        return ResponseEntity.ok(mobilityService.congestionTop3(cinemaId));
    }

    @GetMapping("/mobility/api/geocode")
    @ResponseBody
    public ResponseEntity<MobilityGeoDTO> geocode(@RequestParam String address) {
        return ResponseEntity.ok(mobilityService.geocode(address));
    }

}
