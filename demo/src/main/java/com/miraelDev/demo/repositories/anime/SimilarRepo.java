package com.miraelDev.demo.repositories.anime;

import com.miraelDev.demo.models.dbModels.SimilarAnimeDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimilarRepo extends JpaRepository<SimilarAnimeDbModel,Long> {

}
