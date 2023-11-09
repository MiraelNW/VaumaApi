package com.miraelDev.demo.repositories;

import com.miraelDev.demo.models.dbModels.AnimeDbModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SearchAnimeRepo extends JpaRepository<AnimeDbModel, Long>, JpaSpecificationExecutor<AnimeDbModel> {

    Slice<AnimeDbModel> findByNameStartingWithIgnoreCase(String name, Pageable pageable);

    Slice<AnimeDbModel> findAllBy(Specification<AnimeDbModel> spec, Pageable pageable);

    Slice<AnimeDbModel> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Slice<AnimeDbModel> findByRussianContainingIgnoreCase(String russian, Pageable pageable);

    Slice<AnimeDbModel> findByRussianStartingWithIgnoreCase(String russian, Pageable pageable);

}
