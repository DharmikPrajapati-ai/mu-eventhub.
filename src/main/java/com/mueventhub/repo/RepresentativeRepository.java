package com.mueventhub.repo;

import com.mueventhub.model.Representative;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepresentativeRepository extends JpaRepository<Representative, Long> {
    Optional<Representative> findByUsername(String username);

    boolean existsByUsernameIgnoreCase(String username);
}
