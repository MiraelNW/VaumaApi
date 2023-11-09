package com.miraelDev.demo.servises;

import com.miraelDev.demo.models.dbModels.GenreDbModel;
import com.miraelDev.demo.repositories.GenreRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class GenreService {

    @Autowired
    private GenreRepo repo;

    public void saveAll(Set<GenreDbModel> dbModels) {
        repo.saveAll(dbModels);
    }

}
