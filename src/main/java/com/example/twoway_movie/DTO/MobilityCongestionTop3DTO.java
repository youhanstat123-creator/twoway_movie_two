package com.example.twoway_movie.DTO;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MobilityCongestionTop3DTO {
    private boolean usedApi;
    private String message;
    private List<String> top3;
}
