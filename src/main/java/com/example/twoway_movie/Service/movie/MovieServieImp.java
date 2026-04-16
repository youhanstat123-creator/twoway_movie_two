package com.example.twoway_movie.Service.movie;

import com.example.twoway_movie.Entity.MovieEntity;
import com.example.twoway_movie.Repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MovieServieImp implements MovieServie {

    @Autowired
    private MovieRepository movieRepository;

    /* =========================
       등록
       ========================= */
    @Override
    public void insertp(MovieEntity mentity) {
        movieRepository.save(mentity);
    }

    /* =========================
       전체 조회
       ========================= */
    @Override
    public List<MovieEntity> allout() {
        return movieRepository.findAll();
    }

    /* =========================
       수정 조회
       ========================= */
    @Override
    public MovieEntity updatee(long mbunho) {
        return movieRepository.findById(mbunho).orElse(null);
    }

    /* =========================
       수정 저장
       ========================= */
    @Override
    public void updateae(MovieEntity mentity) {
        movieRepository.save(mentity);
    }

    /* =========================
       삭제
       ========================= */
    @Override
    public void deletea(long mbunho) {
        movieRepository.deleteById(mbunho);
    }

    /* =========================
       상세 (조회수 증가)
       ========================= */
    @Override
    public MovieEntity detail(long mbunho) {
        movieRepository.readcount(mbunho);
        return movieRepository.findById(mbunho).orElse(null);
    }

    @Override
    public void readcount(long mbunho) {
        movieRepository.readcount(mbunho);
    }

    /* =========================
       페이징
       ========================= */
    @Override
    public Page<MovieEntity> entitypage(int page) {
        return movieRepository.findAll(PageRequest.of(page, 8));
    }

    /* =========================
       기본 검색 (제목 / 가격 / 날짜)
       ========================= */
    @Override
    public List<MovieEntity> searchgo2(String mkey, String mvalue) {

        if (mkey == null || mvalue == null) return List.of();

        String key = mkey.trim();
        String val = mvalue.trim();

        try {
            if ("mtitle".equals(key)) {
                return movieRepository.findByMtitleContainingIgnoreCase(val);

            } else if ("mprice".equals(key)) {
                return movieRepository.findByMprice(Integer.parseInt(val));

            } else if ("mdate".equals(key)) {
                return movieRepository.findByMdate(LocalDate.parse(val));
            }
        } catch (Exception e) {
            // 가격/날짜 파싱 실패 시 빈 결과
            return List.of();
        }

        return List.of();
    }

    /* =========================
       ⭐ 장르 텍스트 검색 (핵심)
       ========================= */
    @Override
    public List<MovieEntity> searchByGenreText(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }

        // 대소문자 무시 + 부분 검색
        return movieRepository.findByMgenreContainingIgnoreCase(keyword);
    }
}
