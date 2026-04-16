package com.example.twoway_movie.Controller;

import com.example.twoway_movie.DTO.BoardDTO;
import com.example.twoway_movie.Service.Impl.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 🔥 top 메뉴 호환용
    @GetMapping("/board_outgo")
    public String boardOutgoRedirect() {
        return "redirect:/board_all";
    }

    /* =========================
       등록
       ========================= */
    @GetMapping("/board_inputgo")
    public String boardInput(Model model, Authentication authentication) {
        BoardDTO dto = new BoardDTO();

        if (authentication != null) {
            dto.setBname(authentication.getName());
        }

        model.addAttribute("dto", dto);
        return "board/board_input"; // ✅ templates/board/board_input.html
    }

    @PostMapping("/board_inputgo")
    public String boardInsert(@ModelAttribute BoardDTO dto, Authentication authentication) {
        if (authentication != null) {
            dto.setBname(authentication.getName());
        }
        boardService.insert(dto);
        return "redirect:/board_all";
    }

    /* =========================
       상세
       ========================= */
    @GetMapping("/board_detail")
    public String boardDetail(@RequestParam Long bbunho, Model model) {
        BoardDTO dto = boardService.selectOne(bbunho);
        if (dto == null) return "redirect:/board_all";

        model.addAttribute("dto", dto);
        return "board/board_detail"; // ✅ templates/board/board_detail.html
    }

    /* =========================
       수정
       ========================= */
    @GetMapping("/board_updatego")
    public String boardUpdateGo(@RequestParam Long bbunho, Model model) {
        BoardDTO dto = boardService.selectOne(bbunho);
        if (dto == null) return "redirect:/board_all";

        model.addAttribute("dto", dto);
        return "board/board_update"; // ✅ templates/board/board_update.html
    }

    @PostMapping("/board_update")
    public String boardUpdate(@ModelAttribute BoardDTO dto, Authentication authentication) {

        // ✅ 작성자 조작 방지 (서버에서 고정)
        if (authentication != null) {
            dto.setBname(authentication.getName());
        }

        boardService.update(dto);
        return "redirect:/board_detail?bbunho=" + dto.getBbunho();
    }

    /* =========================
       삭제
       ========================= */
    @GetMapping("/board_delete")
    public String boardDelete(@RequestParam Long bbunho) {
        boardService.delete(bbunho);
        return "redirect:/board_all";
    }

    /* =========================
       답변
       ========================= */
    @GetMapping("/board_replygo")
    public String replyGo(@RequestParam Long bbunho, Model model) {
        BoardDTO dto = boardService.selectOne(bbunho);
        if (dto == null) return "redirect:/board_all";

        model.addAttribute("dto", dto);
        return "board/board_reply"; // ✅ templates/board/board_reply.html
    }

    @PostMapping("/board_reply")
    public String reply(@RequestParam Long bbunho,
                        @RequestParam String breply) {
        boardService.updateReply(bbunho, breply);
        return "redirect:/board_detail?bbunho=" + bbunho;
    }

    /* =========================
       목록 (ALL / MOVIE / HOME)
       ========================= */
    @GetMapping("/board_movie")
    public String boardMovie(@RequestParam(defaultValue = "1") int page, Model model) {
        return boardListByCategory("MOVIE", "🎬 영화 문의 게시판", page, model);
    }

    @GetMapping("/board_home")
    public String boardHome(@RequestParam(defaultValue = "1") int page, Model model) {
        return boardListByCategory("HOME", "🏠 홈페이지 문의 게시판", page, model);
    }

    @GetMapping("/board_all")
    public String boardAll(@RequestParam(defaultValue = "1") int page, Model model) {
        int size = 10;
        int total = boardService.countAll();
        int totalPage = (int) Math.ceil((double) total / size);

        model.addAttribute("list", boardService.pagingAll(page, size));
        model.addAttribute("page", page);
        model.addAttribute("totalPage", Math.max(totalPage, 1));
        model.addAttribute("category", "ALL");
        model.addAttribute("title", "📋 전체 문의 목록");

        return "board/board_inout"; // ✅ templates/board/board_inout.html
    }

    private String boardListByCategory(String category, String title, int page, Model model) {
        int size = 10;
        int total = boardService.countByCategory(category);
        int totalPage = (int) Math.ceil((double) total / size);

        model.addAttribute("list", boardService.pagingByCategory(category, page, size));
        model.addAttribute("page", page);
        model.addAttribute("totalPage", Math.max(totalPage, 1));
        model.addAttribute("category", category);
        model.addAttribute("title", title);

        return "board/board_inout";
    }
}
