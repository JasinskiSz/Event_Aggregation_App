package com.sda.eventapp.service;

import com.sda.eventapp.model.Image;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    private final static Path ABS_PATH = Paths.get("").toAbsolutePath();
    private final static Path IMAGES_PATH = Path.of("src", "main", "resources", "static", "images");
    private final static String DEFAULT_IMAGE_NAME = "default-event-image.jpeg";

    private Image buildImage() {
        return buildImage(DEFAULT_IMAGE_NAME);
    }

    private Image buildImage(String name) {
        return Image.builder()
                .filename(name)
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

    public String wrongFileExtensionMessage() {
        StringBuilder messageSB = new StringBuilder("You must upload file with extension: ");
        AllowedExtensions[] extensions = AllowedExtensions.values();
        IntStream.range(0, extensions.length)
                .mapToObj(i -> extensions[i] + (i < extensions.length - 1 ? ", " : ""))
                .forEach(messageSB::append);
        return messageSB.toString();
    }

    /**
     * Creates directory named after path: {@link com.sda.eventapp.service.ImageService#IMAGES_PATH} if it doesn't already exist.
     *
     * @return true in the same manner as {@link java.io.File#mkdirs()}
     */
    private boolean createImageDirectory() {
        return new File(IMAGES_PATH.toString()).mkdirs();
    }

    /**
     * Taking {@link org.springframework.web.multipart.MultipartFile} and checks its name if it's {@link java.lang.String#isBlank()} or null.
     * <br>
     * If false, it does {@link com.sda.eventapp.service.ImageService#saveImageLocally(MultipartFile)}
     *
     * @param file should be checked before if its of required file extension
     * @return {@link com.sda.eventapp.model.Image} build from {@link org.springframework.web.multipart.MultipartFile}
     */
    public Image solveImage(MultipartFile file) {
        Image image;
        if (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank()) {
            image = buildImage();
        } else {
            image = saveImageLocally(file);
        }
        return image;
    }

    /**
     * Creates directory in default location ->  {@link ImageService#createImageDirectory()}.
     * <br>
     * Changes filename by adding suffix of {@link LocalDateTime#now()} and {@link UUID#randomUUID()}.
     * Replacing all ':' and changing these into '-' via {@link String#replaceAll(String regex, String replacement)}
     * <br>
     * Then builds image with new filename -> {@link ImageService#buildImage(String fileName)}
     * <br>
     *
     * @param file {@link org.springframework.web.multipart.MultipartFile} that is meant to be saved
     *             locally and built {@link com.sda.eventapp.model.Image} out of it
     * @return built and saved locally {@link com.sda.eventapp.model.Image}
     */
    private Image saveImageLocally(MultipartFile file) {
        if (createImageDirectory()) {
            log.debug("Directory created");
        }

        String filename = (LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                + UUID.randomUUID()
                + file.getOriginalFilename())
                .replaceAll(":", "-");

        Image image = buildImage(filename);

        Path fullPath = Path.of(ABS_PATH.toString(), IMAGES_PATH.toString(), image.getFilename());

        try {
            byte[] bytes = file.getBytes();
            Files.write(fullPath, bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return image;
    }

    private enum AllowedExtensions {
        JPG, JPEG, PNG
    }
}
