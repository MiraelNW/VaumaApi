package com.miraelDev.demo.repositories.anime;

import com.miraelDev.demo.models.dbModels.AnimeDbModel;
import com.miraelDev.demo.models.dbModels.VideoDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends JpaRepository<VideoDbModel,Long> {

}
