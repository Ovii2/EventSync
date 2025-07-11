package org.example.backend.repository;

import org.example.backend.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    boolean existsByTitle(String title);
    Page<Event> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
