package com.miraelDev.demo.repositories;

import com.miraelDev.demo.models.dbModels.AnimeDbModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchAnimeRepo extends JpaRepository<AnimeDbModel,Long> {

    Slice<AnimeDbModel> findByNameStartingWithIgnoreCase(String name,Pageable pageable);
    Slice<AnimeDbModel> findByNameContainingIgnoreCase(String name,Pageable pageable);
    Slice<AnimeDbModel> findByRussianContainingIgnoreCase(String russian,Pageable pageable);
    Slice<AnimeDbModel> findByRussianStartingWithIgnoreCase(String russian,Pageable pageable);

}
