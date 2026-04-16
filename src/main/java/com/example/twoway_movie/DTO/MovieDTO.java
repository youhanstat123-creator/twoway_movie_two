package com.example.twoway_movie.DTO;

import com.example.twoway_movie.Entity.MovieEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieDTO {
    long mbunho;
    LocalDate mdate;
    String mtitle;
    String mimage;
    MultipartFile mimage1;
    int mprice;
    int mreadcount;
    String mgenre;
    String mcontent;

    public MovieEntity toentity() {
        MovieEntity me = new MovieEntity();
        me.setMbunho(mbunho);
        me.setMdate(mdate);
        me.setMtitle(mtitle);
        me.setMimage(mimage);
        me.setMprice(mprice);
        me.setMreadcount(mreadcount);
        me.setMgenre(mgenre);
        me.setMcontent(mcontent);
        return me;
    }
}
