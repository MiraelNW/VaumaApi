package com.miraelDev.demo.repositories.anime;

import com.miraelDev.demo.models.dbModels.AnimeDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchAnimeRepository extends JpaRepository<AnimeDbModel, Long>, JpaSpecificationExecutor<AnimeDbModel> {

}
