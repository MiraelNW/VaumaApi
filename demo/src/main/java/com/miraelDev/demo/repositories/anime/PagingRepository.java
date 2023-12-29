package com.miraelDev.demo.repositories.anime;

import com.miraelDev.demo.models.dbModels.AnimeDbModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagingRepository extends JpaRepository<AnimeDbModel, Long> {
    Slice<AnimeDbModel> findAllBy(Pageable pageable);
}
