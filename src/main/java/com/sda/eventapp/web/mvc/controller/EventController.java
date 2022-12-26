package com.sda.eventapp.web.mvc.controller;

import com.sda.eventapp.model.Event;
import com.sda.eventapp.model.Image;
import com.sda.eventapp.service.EventService;
import com.sda.eventapp.web.mvc.form.CreateEventForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping({"/event"})
public class EventController {
    private final EventService eventService;

    @GetMapping("/create")
    public String create(ModelMap model) {
        model.addAttribute("event", new CreateEventForm());
        return "create-event";
    }

    @PostMapping("/create")
    public String handleCreate(@ModelAttribute("event") @Valid CreateEventForm form, Errors errors, @RequestParam MultipartFile img) {
        try {
            String folderForNewDirectory = "src/main/resources/static/images/";
            String folder = "/src/main/resources/static/images/";
            //String folderToDatabase = "/images/";
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
            Path fullPath = Paths.get(image.getPath() + img.getOriginalFilename());
            //Path databasePath = Paths.get(folderToDatabase + img.getOriginalFilename());
            Files.write(fullPath, bytes);


            /*Path fullPath = Paths.get(folder + img.getOriginalFilename());
            Path databasePath = Paths.get(folderToDatabase + img.getOriginalFilename());
            Files.write(fullPath, bytes);*/

/*
            Image image = Image.builder()
                    .fileName(img.getOriginalFilename())
                    .path(String.valueOf(databasePath)) //problem?
                    .build();
*/

            if (errors.hasErrors()) {
                return "create-event";
            }

            Event addedEvent = eventService.save(form, image);

            //todo: managing saving file and creating directory

            if (addedEvent != null) {

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return "index";
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