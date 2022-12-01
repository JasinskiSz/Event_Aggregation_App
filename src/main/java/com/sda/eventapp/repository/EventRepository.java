package com.sda.eventapp.repository;

import com.sda.eventapp.model.Event;
import org.springframework.data.repository.CrudRepository;


public interface EventRepository extends CrudRepository<Event, Long> {
}
