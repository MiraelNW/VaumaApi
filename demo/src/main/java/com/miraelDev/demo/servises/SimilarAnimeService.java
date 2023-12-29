package com.miraelDev.demo.servises;

import com.miraelDev.demo.models.dbModels.SimilarAnimeDbModel;
import com.miraelDev.demo.repositories.anime.SimilarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SimilarAnimeService {

    @Autowired
    private SimilarRepository repo;

    public void saveAll(Set<SimilarAnimeDbModel> dbModels) {
        repo.saveAll(dbModels);
    }

}
