package com.miraelDev.demo.servises;

import com.miraelDev.demo.models.authModels.user.AppUser;
import com.miraelDev.demo.models.dbModels.AnimeDbModel;
import com.miraelDev.demo.models.responseDto.AnimeResponseDto;
import com.miraelDev.demo.repositories.UserRepository;
import com.miraelDev.demo.repositories.anime.AnimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AnimeService {

    @Autowired
    private AnimeRepository animeRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<AnimeResponseDto> getAnimeById(Long animeId, Long userId) {

        AnimeDbModel animeDbModel = animeRepository.getReferenceById(animeId);

        AppUser appUser = userRepository.getReferenceById(userId);

        boolean isFavourite = appUser.getAnimeFavouriteList().contains(animeId);

        return ResponseEntity.ok(AnimeResponseDto.toDtoModel(animeDbModel, isFavourite));
    }

    public ResponseEntity<String> setAnimeFavouriteStatus(Long animeId, Long userId, Boolean isFavourite) {

        boolean isAnimeExist = animeRepository.existsById(animeId);

        if (!isAnimeExist) return ResponseEntity.badRequest().body("anime id is not exist");

        AppUser appUser = userRepository.getReferenceById(userId);

        if (isFavourite) {
            appUser.getAnimeFavouriteList().add(animeId);
        } else {
            appUser.getAnimeFavouriteList().remove(animeId);
        }

        userRepository.save(appUser);

        return ResponseEntity.ok("save");
    }

    public Resource getOriginalImage(Long id) throws IOException {
        Path image = Paths.get("C:\\Users\\1\\Desktop\\animes\\original\\" + id + ".png");
        return new ByteArrayResource(Files.readAllBytes(image));
    }

    public Resource getPreviewImage(Long id) throws IOException {
        Path image = Paths.get("C:\\Users\\1\\Desktop\\animes\\preview\\" + id + ".png");
        return new ByteArrayResource(Files.readAllBytes(image));
    }

    public Resource getUserImage(String username) throws IOException {
        Path image = Paths.get("C:\\Users\\1\\Desktop\\users\\image\\" + username + ".png");
        return new ByteArrayResource(Files.readAllBytes(image));
    }

    public void save(AnimeDbModel dbModel) {
        animeRepository.save(dbModel);
    }

}
