package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.model.Event;
import com.sda.eventapp.model.Image;
import com.sda.eventapp.service.EventService;
import com.sda.eventapp.service.ImageService;
import com.sda.eventapp.web.mvc.form.CreateEventForm;
import jakarta.servlet.ServletException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@Controller
@ControllerAdvice
@RequiredArgsConstructor
@RequestMapping({"/event"})
public class EventController {
    private final EventService eventService;
    private final ImageService imageService;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @GetMapping("/create")
    public String create(ModelMap model) {
        model.addAttribute("event", new CreateEventForm());
        return "create-event";
    }

    @PostMapping("/create")
    public String handleCreate(@ModelAttribute("event") @Valid CreateEventForm form, Errors errors, @RequestParam MultipartFile img, RedirectAttributes ra) {

        if (errors.hasErrors()) {
            return "create-event";
        }
        if (img.getOriginalFilename().isBlank()) {
            String folder = "/src/main/resources/static/images/";
            Path currentPath = Paths.get(""); //on Windows Paths.get(".")
            Path absolutePath = currentPath.toAbsolutePath();
            Image image = Image.builder()
                    .fileName("default-event-image.jpeg")
                    .path(absolutePath + folder)
                    .build();
            eventService.save(form, image);
            return "index";
        }

        String folderForNewDirectory = "src/main/resources/static/images/";
        String folder = "/src/main/resources/static/images/";
        String extension = FilenameUtils.getExtension(img.getOriginalFilename());
        if (!(extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("png"))) {
            ra.addFlashAttribute("wrongExtension", "You must upload jpg or png file");
            return "redirect:/event/create";
        }
        try {
            File directory = new File(folderForNewDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            Path currentPath = Paths.get(""); //on Windows Paths.get(".")
            Path absolutePath = currentPath.toAbsolutePath();
            Image image = Image.builder()
                    .fileName(img.getOriginalFilename())
                    .path(absolutePath + folder)
                    .build();
            byte[] bytes = img.getBytes();
            String originalFilename = img.getOriginalFilename();


            while (imageService.checkImageByFileName(originalFilename)) {
                StringBuilder newNameBuilder = new StringBuilder();
                newNameBuilder.append(RandomStringUtils.random(10, true, false));
                newNameBuilder.append(originalFilename);
                originalFilename = newNameBuilder.toString();
                image.setFileName(originalFilename);

            }
            Path fullPath = Paths.get(image.getPath() + originalFilename);
            Files.write(fullPath, bytes);

            eventService.save(form, image);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "index";
    }

    @ExceptionHandler({MaxUploadSizeExceededException.class})
    public String handleImageUploadError(RedirectAttributes ra) {
        System.out.println("Caught file upload error");
        ra.addFlashAttribute("uploadError", "You could not upload file bigger than " + maxFileSize);
        return "redirect:/event/create";
    }

    @GetMapping("/update/{id}")
    public String update(ModelMap model, @PathVariable Long id) {
        model.addAttribute("event", new CreateEventForm());
        model.addAttribute("event", eventService.findById(id));
        return "update-event";
    }

    @PostMapping("/update")
    public String handleUpdate(@ModelAttribute("event") @Valid CreateEventForm form, Errors errors) {
        if (errors.hasErrors()) {
            return "update-event";
        }
        eventService.update(form);
        return "index";
    }
}