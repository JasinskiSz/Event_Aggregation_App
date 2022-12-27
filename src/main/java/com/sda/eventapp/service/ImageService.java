package com.sda.eventapp.service;

import com.sda.eventapp.model.Image;
import com.sda.eventapp.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository repository;

    public boolean checkImageByFileName(String fileName) {
        return repository.findFirstByFileName(fileName).isPresent();
    }


}
