package com.sda.eventapp.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentView {
    private String text;
    private LocalDateTime writingDate;
    private String userNickname;
}
