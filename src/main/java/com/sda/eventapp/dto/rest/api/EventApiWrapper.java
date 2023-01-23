package com.sda.eventapp.dto.rest.api;

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