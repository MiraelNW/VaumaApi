package com.miraelDev.demo.repositories;

import com.miraelDev.demo.models.dbModels.GenreDbModel;
import com.miraelDev.demo.models.dbModels.SimilarAnimeDbModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SimilarRepo extends JpaRepository<SimilarAnimeDbModel,Long> {

}
