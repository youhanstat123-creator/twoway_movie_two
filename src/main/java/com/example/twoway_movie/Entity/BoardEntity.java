package com.example.twoway_movie.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "TWOWAY_MOVIE_BOARD")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SequenceGenerator(
        name = "board_seq_generator",
        sequenceName = "TWOWAY_MOVIE_BOARD_SEQ",
        allocationSize = 1
)
public class BoardEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "board_seq_generator"
    )
    @Column(name = "BBUNHO")
    private Long bbunho;

    @Column(name = "BNAME")
    private String bname;

    @Temporal(TemporalType.DATE)
    @Column(name = "BDATE")
    private Date bdate;

    @Column(name = "BMEMO")
    private String bmemo;

    @Column(name = "BREPLY")
    private String breply;

    @Column(name = "BCATEGORY")
    private String bcategory;
}
