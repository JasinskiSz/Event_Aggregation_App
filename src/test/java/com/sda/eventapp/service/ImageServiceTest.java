package com.sda.eventapp.service;

import com.sda.eventapp.model.entities.Image;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImageServiceTest {

    ImageService imageService;

    @BeforeEach
    void beforeEach(){
        imageService = new ImageService();
    }

    @ParameterizedTest
    @ValueSource(strings = {"image1.jpg", "image2.png", "image3.jpeg"})
    void shouldReturnTrueIfImageExtensionsCorrect(String input) {
        //given
        MockMultipartFile multipartFile = new MockMultipartFile("mockName", input, (String) null, (byte[]) null);
        //when
        boolean actual = imageService.isImage(multipartFile);
        //then
        assertTrue(actual);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"file1.txt", "file2.pdf"})
    void shouldReturnFalseIfImageExtensionsImcorrect(String input) {
        //given
        MockMultipartFile multipartFile = new MockMultipartFile("mockName", input, (String) null, (byte[]) null);
        //when
        boolean actual = imageService.isImage(multipartFile);
        //then
        assertFalse(actual);
    }

    @Test
    void shouldReturnWrongFileExtensionMessage() {
        //when
        String actual = imageService.wrongFileExtensionMessage();
        //then
        assertThat(actual).isEqualTo("You must upload file with extension: JPG, JPEG, PNG");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    void shouldAssignDefaultImageName(String input) {
        //given
        MockMultipartFile multipartFile = new MockMultipartFile("mockName", input, (String) null, (byte[]) null);
        //when
        Image actual = imageService.solveImage(multipartFile);
        //then
        assertThat(actual.getFilename()).isEqualTo("default-event-image.jpeg");
    }

    @ParameterizedTest
    @ValueSource(strings = {"defaultName", ".", ","})
    void shouldAssignImageName(String input) {
        //given
        MockMultipartFile multipartFile = new MockMultipartFile("mockName", input, (String) null, (byte[]) null);
        //when
        Image actual = imageService.solveImage(multipartFile);
        //then
        assertThat(actual.getFilename().length()).isEqualTo(input.length() + 55);
        assertThat(actual.getFilename()).endsWith(input);
    }

}