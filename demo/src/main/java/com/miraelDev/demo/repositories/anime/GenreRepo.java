package com.miraelDev.demo.repositories.anime;

import com.miraelDev.demo.models.dbModels.GenreDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepo extends JpaRepository<GenreDbModel,Long> {

}
