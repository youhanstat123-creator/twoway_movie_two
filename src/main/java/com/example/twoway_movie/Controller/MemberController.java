package com.example.twoway_movie.Controller;

import com.example.twoway_movie.DTO.MemberDTO;
import com.example.twoway_movie.Entity.MemberEntity;
import com.example.twoway_movie.Service.Member.MemberService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
public class MemberController {

    @Autowired
    MemberService memberService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/member_inputgo")
    public String member1(Model model) {
        model.addAttribute("memberDTO", new MemberDTO());
        return "member/memberinput";
    }

    @PostMapping("/memberinputsave")
    public String member2(@Valid @ModelAttribute("memberDTO") MemberDTO memberDTO,
                          BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(e ->
                    log.info("VALIDATION ERROR field={} msg={} value={}",
                            e.getField(), e.getDefaultMessage(), e.getRejectedValue())
            );
            return "member/memberinput";
        }

        memberService.memberinsert(memberDTO);
        return "redirect:/login";
    }

    @GetMapping("/admin/item")
    public String adminItem() {
        return "admin/item";
    }

    // ✅ 회원정보출력(페이징)
    @GetMapping("/member_outgo")
    public String memberOut(Model model,
                            @RequestParam(required = false, defaultValue = "0", value = "page") int page) {

        Page<MemberEntity> listPage = memberService.entitypage(page);

        int totalPage = listPage.getTotalPages();
        int nowpage = listPage.getNumber() + 1;

        model.addAttribute("nowpage", nowpage);
        model.addAttribute("list", listPage.getContent());
        model.addAttribute("totalPage", totalPage);

        return "member/memberout";
    }

    @GetMapping("/map_go")
    public String mapGo() {
        return "map";
    }

    /**
     * ✅ 회원검색 화면 + 결과 (GET)
     * /member_searchgo
     * /member_searchgo?mkey=userid&mvalue=hoho
     */
    @GetMapping("/member_searchgo")
    public String memberSearchGo(
            @RequestParam(required = false, defaultValue = "userid") String mkey,
            @RequestParam(required = false) String mvalue,
            Model model
    ) {
        model.addAttribute("mkey", mkey);
        model.addAttribute("mvalue", mvalue);

        if (mvalue == null || mvalue.trim().isEmpty()) {
            model.addAttribute("list", Collections.emptyList());
            return "member/membersearch";
        }

        List<MemberEntity> list = memberService.search(mkey, mvalue);
        model.addAttribute("list", list);

        return "member/membersearch";
    }

    // ✅ form이 POST로 와도 동일 처리
    @PostMapping("/member_searchgo")
    public String memberSearchPost(
            @RequestParam("mkey") String mkey,
            @RequestParam("mvalue") String mvalue,
            Model model
    ) {
        return memberSearchGo(mkey, mvalue, model);
    }

    // ✅ 레거시 URL 방어
    @PostMapping("/membersearchsave")
    public String legacySearch(@RequestParam("mkey") String mkey,
                               @RequestParam("mvalue") String mvalue) {
        return "redirect:/member_searchgo?mkey=" + mkey + "&mvalue=" + mvalue;
    }
}
