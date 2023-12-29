package com.miraelDev.demo.servises;

import com.miraelDev.demo.models.dbModels.GenreDbModel;
import com.miraelDev.demo.repositories.anime.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class GenreService {

    @Autowired
    private GenreRepository repo;

    public void saveAll(Set<GenreDbModel> dbModels) {
        repo.saveAll(dbModels);
    }

}
