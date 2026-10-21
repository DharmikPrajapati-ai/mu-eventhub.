package com.mueventhub.repo;

import com.mueventhub.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByOrderByStartDateAsc();

    List<Event> findByCollegeIdOrderByStartDateAsc(Long collegeId);

    long countByCollegeIdAndEndDateGreaterThanEqual(Long collegeId, LocalDate date);

    long countByEndDateGreaterThanEqual(LocalDate date);
}
