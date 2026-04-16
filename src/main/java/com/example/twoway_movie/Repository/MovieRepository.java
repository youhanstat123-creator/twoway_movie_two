package com.example.twoway_movie.Repository;

import com.example.twoway_movie.Entity.MovieEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MovieRepository extends JpaRepository<MovieEntity, Long> {

    /* =========================
       조회수 증가
       ========================= */
    @Transactional
    @Modifying
    @Query(
            value = "UPDATE TWOWAY_MOVIE " +
                    "SET mreadcount = mreadcount + 1 " +
                    "WHERE mbunho = :mbunho",
            nativeQuery = true
    )
    void readcount(@Param("mbunho") long mbunho);

    /* =========================
       기본 조회
       ========================= */
    MovieEntity findByMbunho(long mbunho);

    List<MovieEntity> findByMdate(LocalDate mdate);

    /* =========================
       검색 - 제목 / 가격
       ========================= */

    // 제목 검색 (대소문자 무시)
    List<MovieEntity> findByMtitleContainingIgnoreCase(String mtitle);

    // 가격 검색
    List<MovieEntity> findByMprice(int mprice);

    /* =========================
       ⭐ 장르 검색 (핵심)
       ========================= */

    // 장르 텍스트 검색 (ROMANCE / ani / FANTASY 등)
    List<MovieEntity> findByMgenreContainingIgnoreCase(String mgenre);

    /* =========================
       통합 검색 (mkey 기반)
       ========================= */
    @Query("""
        select m from MovieEntity m
        where
            (:mkey = 'mtitle' and lower(m.mtitle) like lower(concat('%', :mvalue, '%')))
         or (:mkey = 'mprice' and cast(m.mprice as string) like concat('%', :mvalue, '%'))
         or (:mkey = 'mdate' and cast(m.mdate as string) like concat('%', :mvalue, '%'))
    """)
    List<MovieEntity> searchgo2(
            @Param("mkey") String mkey,
            @Param("mvalue") String mvalue
    );
}
