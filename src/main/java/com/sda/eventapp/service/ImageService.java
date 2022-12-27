package com.sda.eventapp.service;

import com.sda.eventapp.model.Image;
import com.sda.eventapp.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository repository;

    public boolean checkImageByFileName(String fileName) {
        return repository.findFirstByFileName(fileName).isPresent();
    }

    public Image buildDefaultImage(String folder, Path absolutePath) {
        Image image = Image.builder()
                .fileName("default-event-image.jpeg")
                .path(absolutePath + folder)
                .build();
        return image;
    }

    public Image buildImage(MultipartFile img, String folder, Path absolutePath) {
        Image image = Image.builder()
                .fileName(img.getOriginalFilename())
                .path(absolutePath + folder)
                .build();
        return image;
    }

}
