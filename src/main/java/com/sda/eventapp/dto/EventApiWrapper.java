package com.sda.eventapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class EventApiWrapper {
    private List<EventApi> eventApiList;
}