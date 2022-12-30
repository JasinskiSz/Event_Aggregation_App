package com.sda.eventapp.service;

import com.sda.eventapp.model.Image;
import com.sda.eventapp.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository repository;

    public boolean existsByFilename(String filename) {
        return repository.existsByFilename(filename);
    }

    public Image buildDefaultImage(Path absolutePath, String contentRootPath) {
        return Image.builder()
                .filename("default-event-image.jpeg")
                .path(absolutePath + "/" + contentRootPath)
                .build();
    }

    public Image buildImage(String name, Path absolutePath, String contentRootPath) {
        return Image.builder()
                .filename(name)
                .path(absolutePath + "/" + contentRootPath)
                .build();
    }

    /**
     * Returns {@code true} if the file extension matches with any value in the enum {@link com.sda.eventapp.service.ImageService.AllowedExtensions}.
     *
     * @param file A {@link org.springframework.web.multipart.MultipartFile} from which the file extension will be taken
     * @return true if file extension matches any value from AllowedExtensions
     */
    public boolean isImage(MultipartFile file) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());

        return Arrays.stream(AllowedExtensions.values())
                .anyMatch(e -> e.toString().equalsIgnoreCase(extension));
    }

    private enum AllowedExtensions {
        JPG, JPEG, PNG
    }
}
