package com.sda.eventapp.repository;

import com.sda.eventapp.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
    boolean existsByFileName(String fileName);
}
