package com.miraelDev.demo;

import com.miraelDev.demo.servises.AnimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
public class MediaContentController {

    @Autowired
    private AnimeService animeService;


    @GetMapping(value = "/anime/images/original/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public Resource getOriginalImageById(@PathVariable Long id) throws IOException {
        return animeService.getOriginalImage(id);
    }

    @GetMapping(value = "/anime/images/preview/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public Resource getPreviewImageById(@PathVariable Long id) throws IOException {
        return animeService.getPreviewImage(id);
    }

    @GetMapping(value = "users/image/{username}", produces = MediaType.IMAGE_PNG_VALUE)
    public Resource getUserImageByUserName(@PathVariable String username) throws IOException {
        return animeService.getUserImage(username);
    }

}