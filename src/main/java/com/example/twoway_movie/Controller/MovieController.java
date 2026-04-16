package com.example.twoway_movie.Controller;

import com.example.twoway_movie.DTO.MovieDTO;
import com.example.twoway_movie.Entity.MovieEntity;
import com.example.twoway_movie.Service.movie.MovieServie;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class MovieController {

    @Value("${app.upload-dir}")
    private String uploadDir;

    private final MovieServie movieServie;

    /* =========================
       공통 유틸
       ========================= */
    private File ensureUploadDir() {
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    private String safeFilename(MultipartFile mf) {
        String origin = (mf.getOriginalFilename() == null)
                ? "poster.jpg"
                : mf.getOriginalFilename().trim();
        origin = origin.replace("\\", "_").replace("/", "_");
        return UUID.randomUUID() + "_" + origin;
    }

    private void deleteFileIfExists(String filename) {
        if (filename == null || filename.isBlank()) return;
        File dir = ensureUploadDir();
        File f = new File(dir, filename);
        if (f.exists()) f.delete();
    }

    /* =========================
       1) 메인
       ========================= */
    @GetMapping("/")
    public String main(Model model) {
        List<MovieEntity> all = movieServie.allout();

        if (all != null) {
            all.sort(Comparator.comparing(MovieEntity::getMbunho).reversed());
            if (all.size() > 24) all = all.subList(0, 24);
        }
        model.addAttribute("movies", all);
        return "main";
    }

    /* =========================
       2) 입력 화면
       ========================= */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/mv_inputgo")
    public String mvInputGo() {
        return "movie/mv_input";
    }

    /* =========================
       3) 입력 저장 (복수 장르)
       ========================= */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/mv_inputsave")
    public String mvInputSave(
            MovieDTO mdto,
            @RequestParam(value = "mgenre", required = false) String[] mgenre
    ) throws IOException {

        if (mgenre == null || mgenre.length == 0) {
            return "redirect:/mv_inputgo";
        }
        mdto.setMgenre(String.join(",", mgenre));

        MultipartFile mf = mdto.getMimage1();
        if (mf == null || mf.isEmpty()) {
            return "redirect:/mv_inputgo";
        }

        File dir = ensureUploadDir();
        String fname = safeFilename(mf);

        mdto.setMimage(fname);
        MovieEntity entity = mdto.toentity();
        movieServie.insertp(entity);

        mf.transferTo(new File(dir, fname));
        return "redirect:/mv_outgo";
    }

    /* =========================
       4) 목록
       ========================= */
    @GetMapping("/mv_outgo")
    public String mvOutGo(Model model,
                          @RequestParam(value = "page", defaultValue = "0") int page) {

        Page<MovieEntity> listPage = movieServie.entitypage(page);

        model.addAttribute("list", listPage.getContent());
        model.addAttribute("nowpage", listPage.getNumber() + 1);
        model.addAttribute("totalPage", listPage.getTotalPages());

        return "movie/mv_out";
    }

    /* =========================
       5) 수정 화면
       ========================= */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/update")
    public String update(@RequestParam("number") long mbunho, Model model) {

        MovieEntity entity = movieServie.updatee(mbunho);
        if (entity == null) return "redirect:/mv_outgo";

        model.addAttribute("movie", entity);
        return "movie/mv_update";
    }

    /* =========================
       6) 수정 저장 (복수 장르)
       ========================= */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/updateSave")
    public String updateSave(
            MovieDTO mdto,
            @RequestParam(value = "mgenre", required = false) String[] mgenre,
            @RequestParam(value = "oldimage", required = false) String oldimage
    ) throws IOException {

        MovieEntity entity = movieServie.updatee(mdto.getMbunho());
        if (entity == null) return "redirect:/mv_outgo";

        if (mgenre != null && mgenre.length > 0) {
            entity.setMgenre(String.join(",", mgenre));
        }

        MultipartFile mf = mdto.getMimage1();
        String finalName = oldimage;
        File dir = ensureUploadDir();

        if (mf != null && !mf.isEmpty()) {
            deleteFileIfExists(oldimage);
            finalName = safeFilename(mf);
            mf.transferTo(new File(dir, finalName));
        }

        entity.setMtitle(mdto.getMtitle());
        entity.setMprice(mdto.getMprice());
        entity.setMdate(mdto.getMdate());
        entity.setMimage(finalName);

        movieServie.updateae(entity);
        return "redirect:/mv_outgo";
    }

    /* =========================
       7) 삭제
       ========================= */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/delete")
    public String delete(@RequestParam("number") long mbunho,
                         @RequestParam(value = "delimage", required = false) String delimage) {

        movieServie.deletea(mbunho);
        deleteFileIfExists(delimage);
        return "redirect:/mv_outgo";
    }

    /* =========================
       8) 상세
       ========================= */
    @GetMapping("/detail")
    public String detail(@RequestParam("number") long mbunho, Model model) {

        MovieEntity entity = movieServie.detail(mbunho);
        if (entity == null) return "redirect:/mv_outgo";

        model.addAttribute("dto", entity);
        return "movie/mv_detailview";
    }

    /* =========================
       9) 검색
       ========================= */
    @GetMapping("/mv_searchgo")
    public String mvSearchGo() {
        return "movie/mv_search";
    }

    @GetMapping("/searchgo1")
    public String searchGoGet(
            @RequestParam(value = "mkey", required = false) String mkey,
            @RequestParam(value = "mvalue", required = false) String mvalue,
            Model model) {

        return doSearch(mkey, mvalue, model);
    }

    @PostMapping("/searchgo1")
    public String searchGoPost(
            @RequestParam(value = "mkey", required = false) String mkey,
            @RequestParam(value = "mvalue", required = false) String mvalue,
            Model model) {

        return doSearch(mkey, mvalue, model);
    }

    /* =========================
       검색 핵심 로직
       ========================= */
    private String doSearch(String mkey, String mvalue, Model model) {

        if (mkey == null || mkey.isBlank()
                || mvalue == null || mvalue.isBlank()) {

            model.addAttribute("list", List.of());
            model.addAttribute("mkey", mkey);
            model.addAttribute("mvalue", mvalue);
            return "movie/mv_searchout";
        }

        List<MovieEntity> list;

        // ⭐ 장르 텍스트 검색
        if ("genre".equals(mkey)) {
            String keyword = mvalue.trim().toUpperCase();
            list = movieServie.searchByGenreText(keyword);

        } else {
            // 제목 / 가격 / 날짜
            list = movieServie.searchgo2(mkey, mvalue);
        }

        model.addAttribute("list", (list == null) ? List.of() : list);
        model.addAttribute("mkey", mkey);
        model.addAttribute("mvalue", mvalue);

        return "movie/mv_searchout";
    }
}
