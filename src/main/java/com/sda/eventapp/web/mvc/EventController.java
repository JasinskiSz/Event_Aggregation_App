package com.sda.eventapp.web.mvc;

import com.sda.eventapp.model.Event;
import com.sda.eventapp.model.Image;
import com.sda.eventapp.service.EventService;
import com.sda.eventapp.web.mvc.form.CreateEventForm;
import com.sda.eventapp.web.mvc.mappers.EventMapper;
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
@RequestMapping({"/create/event"})
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping
    public String create(ModelMap model) {
        model.addAttribute("event", new CreateEventForm());
        return "create-event";
    }

    @PostMapping
    public String handleCreate(@ModelAttribute("event") @Valid CreateEventForm form, Errors errors, @RequestParam MultipartFile img) {
        Image image = Image.builder()
                .name(img.getOriginalFilename())
                .build();


        if (errors.hasErrors()) {
            return "create-event";
        }
        Event addedEvent = eventService.save(EventMapper.toEntity(form, image));

        //todo: managing saving file and creating directory

        if(addedEvent != null){


            /*File f = new File("/Users/pavankumar/Desktop/Testing/Java.txt");
            f.createNewFile();*/




            try {
                //MultipartFile multipartFile = new MockMultipartFile("sourceFile.tmp", "Hello World".getBytes());

                //File file = new File("src/main/resources/targetFile.tmp");


                String pathName = "src/main/resources/static/images/";
                new File(pathName).mkdir();
                File f = new File(pathName + img.getOriginalFilename());
                //File f = new File("src/main/resources/static/images/" + img.getOriginalFilename());
                img.transferTo(f );
                f.createNewFile();


                //File saveFile = new ClassPathResource("/Users/apple/Desktop/SDA_kurs_Javy/Spring/Event_Aggregation_Service/src/main/resources/static/eventImage").getFile();
               /* Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + img.getOriginalFilename());
                Files.copy(img.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);*/
            } catch (IOException e) {
                throw new RuntimeException(e);
            }





            /*String newPath = "/Users/apple/Desktop/SDA_kurs_Javy/Spring/Event_Aggregation_Service/src/main/resources/static/eventImage";
            File directory = new File(newPath);
            if(!directory.exists()){
                directory.mkdirs();
            }
            try {
                System.out.println(new ClassPathResource("").getFile().getAbsolutePath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try{
             File saveFile = new ClassPathResource(newPath).getFile(); //todo change path

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + img.getOriginalFilename());

                Files.copy(img.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            catch (Exception e){
                e.printStackTrace();
            }*/
        }

        return "index";
    }


}