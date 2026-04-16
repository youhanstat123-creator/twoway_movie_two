package com.example.twoway_movie.Service.movie;

import com.example.twoway_movie.Entity.MovieEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MovieServie {
    void insertp(MovieEntity mentity);

    List<MovieEntity> allout();

    MovieEntity updatee(long mbunho);

    void updateae(MovieEntity entity);

    void deletea(long mbunho);

    MovieEntity detail(long mbunho);

    void readcount(long mbunho);

    Page<MovieEntity> entitypage(int page);

    List<MovieEntity> searchgo2(String mkey, String mvalue);

    List<MovieEntity> searchByGenreText(String keyword);
}
