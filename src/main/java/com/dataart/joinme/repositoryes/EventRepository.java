package com.dataart.joinme.repositoryes;

import com.dataart.joinme.models.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.time.Instant;

public interface EventRepository extends CrudRepository<Event, Long> {
    Page<Event> findEventsByDateAfter(Instant date, Pageable pageable);
}
