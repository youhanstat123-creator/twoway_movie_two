package com.example.twoway_movie.DTO;

import com.example.twoway_movie.Entity.BoardEntity;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardDTO {

    private Long bbunho;
    private String bname;
    private Date bdate;
    private String bmemo;
    private String breply;
    private String bcategory;

    /** DTO → Entity */
    public BoardEntity toEntity() {
        return BoardEntity.builder()
                .bbunho(this.bbunho)
                .bname(this.bname)
                .bdate(this.bdate)
                .bmemo(this.bmemo)
                .breply(this.breply)
                .bcategory(this.bcategory)
                .build();
    }

    /** Entity → DTO */
    public static BoardDTO fromEntity(BoardEntity e) {
        return BoardDTO.builder()
                .bbunho(e.getBbunho())
                .bname(e.getBname())
                .bdate(e.getBdate())
                .bmemo(e.getBmemo())
                .breply(e.getBreply())
                .bcategory(e.getBcategory())
                .build();
    }
}
