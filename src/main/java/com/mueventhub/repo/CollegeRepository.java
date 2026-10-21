package com.mueventhub.repo;

import com.mueventhub.model.College;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollegeRepository extends JpaRepository<College, Long> {
    Optional<College> findByNameIgnoreCase(String name);

    List<College> findAllByOrderByNameAsc();
}
