package com.miraelDev.demo.repositories;

import com.miraelDev.demo.models.dbModels.AnimeDbModel;
import com.miraelDev.demo.models.dbModels.GenreDbModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepo extends JpaRepository<GenreDbModel,Long> {

}
