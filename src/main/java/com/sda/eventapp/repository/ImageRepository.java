package com.sda.eventapp.repository;

import com.sda.eventapp.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {


    Optional<Image> findFirstByFileName(String fileName);
}
