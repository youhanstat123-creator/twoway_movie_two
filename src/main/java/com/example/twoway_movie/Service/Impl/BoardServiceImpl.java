package com.example.twoway_movie.Service.Impl;

import com.example.twoway_movie.DTO.BoardDTO;
import com.example.twoway_movie.Entity.BoardEntity;
import com.example.twoway_movie.Repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;

    @Override
    public void insert(BoardDTO dto) {
        BoardEntity entity = dto.toEntity();
        entity.setBdate(new Date());
        boardRepository.save(entity);
    }

    @Override
    public List<BoardDTO> selectAll() {
        return boardRepository.findAll(Sort.by(Sort.Direction.DESC, "bbunho"))
                .stream()
                .map(BoardDTO::fromEntity)
                .toList();
    }

    @Override
    public BoardDTO selectOne(Long bbunho) {
        return boardRepository.findById(bbunho)
                .map(BoardDTO::fromEntity)
                .orElse(null);
    }

    @Override
    public void update(BoardDTO dto) {

        BoardEntity entity = boardRepository.findById(dto.getBbunho())
                .orElseThrow(() -> new IllegalArgumentException("해당 글이 없습니다"));

        entity.setBname(dto.getBname());
        entity.setBmemo(dto.getBmemo());
        entity.setBcategory(dto.getBcategory());

        boardRepository.save(entity);
    }



    @Override
    public void delete(Long bbunho) {
        boardRepository.deleteById(bbunho);
    }

    @Override
    public void updateReply(Long bbunho, String breply) {
        BoardEntity entity = boardRepository.findById(bbunho).orElseThrow();
        entity.setBreply(breply);
        boardRepository.save(entity);
    }

    @Override
    public List<BoardDTO> selectByCategory(String category) {
        return boardRepository
                .findByBcategory(category, PageRequest.of(0, Integer.MAX_VALUE))
                .stream()
                .map(BoardDTO::fromEntity)
                .toList();
    }

    @Override
    public List<BoardDTO> pagingAll(int page, int size) {
        return boardRepository
                .findAll(PageRequest.of(page - 1, size, Sort.by("bbunho").descending()))
                .stream()
                .map(BoardDTO::fromEntity)
                .toList();
    }

    @Override
    public List<BoardDTO> pagingByCategory(String category, int page, int size) {
        return boardRepository
                .findByBcategory(category,
                        PageRequest.of(page - 1, size, Sort.by("bbunho").descending()))
                .stream()
                .map(BoardDTO::fromEntity)
                .toList();
    }

    @Override
    public int countAll() {
        return (int) boardRepository.count();
    }

    @Override
    public int countByCategory(String category) {
        return (int) boardRepository.countByBcategory(category);
    }
}
