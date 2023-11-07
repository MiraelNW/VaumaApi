package com.miraelDev.demo.servises;

import com.miraelDev.demo.models.dbModels.AnimeDbModel;
import com.miraelDev.demo.repositories.AnimeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AnimeService {

    @Autowired
    private AnimeRepo repo;

    public AnimeDbModel getUser(Long id) {
        return repo.getReferenceById(id);
    }

    public Resource getOriginalImage(Long id) throws IOException {
        Path image = Paths.get("C:\\Users\\1\\Desktop\\animes\\original\\" + id + ".png");
        return new ByteArrayResource(Files.readAllBytes(image));
    }

    public Resource getPreviewImage(Long id) throws IOException {
        Path image = Paths.get("C:\\Users\\1\\Desktop\\animes\\preview\\" + id + ".png");
        return new ByteArrayResource(Files.readAllBytes(image));
    }

    public void save(AnimeDbModel dbModel) {
        repo.save(dbModel);
    }

}
