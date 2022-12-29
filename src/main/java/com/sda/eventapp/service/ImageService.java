package com.sda.eventapp.service;

import com.sda.eventapp.model.Image;
import com.sda.eventapp.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository repository;

    public boolean checkImageByFileName(String fileName) {
        return repository.existsByFileName(fileName);
    }

    public Image buildDefaultImage(String folder, Path absolutePath) {
        return Image.builder()
                .fileName("default-event-image.jpeg")
                .path(absolutePath + folder)
                .build();
    }

    public Image buildImage(MultipartFile img, String folder, Path absolutePath) {
        return Image.builder()
                .fileName(img.getOriginalFilename())
                .path(absolutePath + folder)
                .build();
    }
}
