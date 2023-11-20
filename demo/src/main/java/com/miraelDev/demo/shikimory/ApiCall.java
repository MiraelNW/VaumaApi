package com.miraelDev.demo.shikimory;

import com.miraelDev.demo.shikimory.dto.AnimeInfoDto;
import com.miraelDev.demo.shikimory.dto.SimilarAnimeDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public interface ApiCall {

    @GET("animes/{id}")
    Call<AnimeInfoDto> getAnime(@Path("id") int id);

    @GET("animes/{id}/similar")
    Call<List<SimilarAnimeDto>> getSimilarAnime(@Path("id") int id);

}
