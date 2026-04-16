package com.example.twoway_movie.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SequenceGenerator(
        name = "movie",
        sequenceName = "movie_seq",
        initialValue = 1000,
        allocationSize = 1
)
@Table(name = "TWOWAY_MOVIE")
public class MovieEntity {
    @Id
    @GeneratedValue(strategy =GenerationType.SEQUENCE,generator = "movie")
    @Column
    long mbunho;
    @Column
    LocalDate mdate = LocalDate.now();
    @Column
    String mtitle;
    @Column
    String mimage;
    @Column
    int mprice;
    @Column
    int mreadcount;
    @Column
    String mgenre;
    @Column
    String mcontent;

}
