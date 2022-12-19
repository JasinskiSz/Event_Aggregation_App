package com.sda.eventapp.web.mvc.mappers;

import com.sda.eventapp.model.Event;
import com.sda.eventapp.model.Image;
import com.sda.eventapp.web.mvc.form.CreateEventForm;
import org.springframework.web.multipart.MultipartFile;

public class EventMapper {
    public static Event toEntity(CreateEventForm form, Image img) {
        return Event.builder()
                .title(form.getTitle())
                .description(form.getDescription())
                .startingDateTime(form.getStartEventDate())
                .endingDateTime(form.getEndEventDate())
                .image(img)
                .build();
        //return new Event(form.getTitle(), form.getDescription(), form.getStartEventDate(), form.getEndEventDate());
    }
}