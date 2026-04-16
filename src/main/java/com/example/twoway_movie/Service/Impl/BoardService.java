package com.example.twoway_movie.Service.Impl;

import com.example.twoway_movie.DTO.BoardDTO;

import java.util.List;

public interface BoardService {

    void insert(BoardDTO dto);

    List<BoardDTO> selectAll();

    BoardDTO selectOne(Long bbunho);

    void update(BoardDTO dto);

    void delete(Long bbunho);

    void updateReply(Long bbunho, String breply);

    List<BoardDTO> selectByCategory(String category);

    List<BoardDTO> pagingAll(int page, int size);

    List<BoardDTO> pagingByCategory(String category, int page, int size);

    int countAll();

    int countByCategory(String category);
}
