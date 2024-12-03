package com.momentum.repository;

import com.momentum.domain.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SprintRepository extends JpaRepository<Sprint, Long> {
    boolean existsByProjectIdAndName(Long projectId, String name);
}
