package com.miraelDev.demo.servises;

import com.miraelDev.demo.models.dbModels.VideoDbModel;
import com.miraelDev.demo.repositories.anime.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    public void saveAll(Set<VideoDbModel> dbModels) {
        videoRepository.saveAll(dbModels);
    }

}
