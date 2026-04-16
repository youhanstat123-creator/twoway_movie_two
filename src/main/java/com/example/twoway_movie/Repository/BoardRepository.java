package com.example.twoway_movie.Repository;

import com.example.twoway_movie.Entity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {

    Page<BoardEntity> findByBcategory(String category, Pageable pageable);

    long countByBcategory(String category);
}
