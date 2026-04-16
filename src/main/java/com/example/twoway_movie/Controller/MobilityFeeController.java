package com.example.twoway_movie.Controller;

import com.example.twoway_movie.DTO.fee.FeeDtos.*;
import com.example.twoway_movie.Service.fee.CertService;
import com.example.twoway_movie.Service.fee.FeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mobility") // ✅ 여기로 통일
public class MobilityFeeController {

    private final FeeService feeService;
    private final CertService certService;

    // ✅ 페이지
    @GetMapping("/fee") // ✅ /mobility/fee
    public String feePage(Model model) {

        model.addAttribute("parkingOptions", new String[]{
                "공영주차장(P1)", "공영주차장(P2)", "공영주차장(P3)"
        });

        // ✅ 템플릿 경로 권장: templates/mobility/mobility_fee.html
        return "mobility/mobility_fee";
    }

    // ✅ 정산(계산)
    @PostMapping("/api/fee/calculate")
    @ResponseBody
    public CalculateRes calculate(@RequestBody CalculateReq req) {
        return feeService.calculate(req);
    }

    // ✅ 인증코드 발급(세션에 묶임 / 30분)
    @PostMapping("/api/cert/issue")
    @ResponseBody
    public IssueRes issue(@RequestBody IssueReq req) {
        return certService.issue(req);
    }

    // ✅ 인증코드 검증(Used 처리 X)
    @PostMapping("/api/cert/verify")
    @ResponseBody
    public VerifyRes verify(@RequestBody VerifyReq req) {
        return certService.verify(req);
    }

    // ✅ 출차 승인(여기서 USED 확정)
    @PostMapping("/api/fee/approve-exit")
    @ResponseBody
    public ApproveRes approveExit(@RequestBody ApproveReq req) {
        return feeService.approveExit(req);
    }

    // ✅ 출차 완료
    @PostMapping("/api/fee/exit-complete")
    @ResponseBody
    public ExitCompleteRes exitComplete(@RequestBody ExitCompleteReq req) {
        return feeService.exitComplete(req);
    }
}
