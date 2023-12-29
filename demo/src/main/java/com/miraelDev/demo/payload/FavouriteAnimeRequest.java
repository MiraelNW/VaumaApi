package com.miraelDev.demo.payload;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class FavouriteAnimeRequest {

    private Long anime_id;
    private Long user_id;
    private Boolean is_favourite;
}